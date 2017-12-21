
package Server;

import com.google.gson.Gson;
import Common.Message;
import Common.MessageException;
import Common.MessageType;
import Common.ThreadSafeStdOut;
import acm.util.RandomGenerator;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable {

    private SocketChannel clientSocket;
    private final ThreadSafeStdOut consoleOut = new ThreadSafeStdOut();
    private Gson g = new Gson();
    private HangmanWord wl;
    private String clientIdentifier;
    private LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private static final int MAX_MESSAGE_SIZE = 512;
    private final ByteBuffer msgFromClient = ByteBuffer.allocateDirect(MAX_MESSAGE_SIZE);
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque();
    private HangmanServer server;

    public ClientHandler(HangmanServer server, SocketChannel clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        try {
            InetSocketAddress addr = (InetSocketAddress) clientSocket.getRemoteAddress();
            clientIdentifier = addr.getAddress().getHostAddress() + ":" + addr.getPort();
            consoleOut.println("Client connected from " + clientIdentifier);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public void run() {
        try {
            if (!messages.isEmpty()) {
                Message msg = messages.take();

                if (msg.getMsgType() == null) {
                    Message m = new Message(MessageType.INFO, "Invalid message!");
                    prepareToSend(m);
                } else {
                    switch (msg.getMsgType()) {
                        case QUIT:
                            disconnectClient();
                            break;
                        case START:
                            startNewGame(msg);
                            break;
                        case GUESS:
                            if (msg.isGameRunning()) {
                                makeGuess(msg);
                            } else {
                                Message m = new Message(MessageType.INFO, "Type Start to begin a new game!", "", 0, msg.getScore(), false);
                                prepareToSend(m);
                            }
                            break;
                        default:
                            Message m = new Message(MessageType.INFO, "Unknown command!");
                            prepareToSend(m);
                            // consoleOut.println("Received unknown command from: " + clientIdentifier);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startNewGame(Message oldMessage) {
        consoleOut.println("Starting a new game for client " + clientIdentifier);
        boolean gameRunning = true;
        int score = oldMessage.getScore();
        String secretWord = pickWord();
        String hiddenWord = showNumberOfLetters(secretWord);
        int secretWordLength = secretWord.length();
     
        Message msg;
        msg = new Message(MessageType.GAMEINFO, "Game started!", hiddenWord, secretWord, secretWordLength, score, gameRunning);
        consoleOut.println(clientIdentifier + " : " + secretWord);
        prepareToSend(msg);
    }

    private void makeGuess(Message msg) {
        String guess = msg.getMessage().toUpperCase();
        String secretWord = msg.getSecretWord().toUpperCase();
        String hiddenWord = msg.getHiddenWord();
        int score = msg.getScore();
        int secretWordLength = secretWord.length();
        int remainingAttempts = msg.getRemainingAttempts();
        boolean gameRunning;
        System.out.println("guess: "+ guess + " secretWord: "+secretWord+" hiddenWord: "+hiddenWord);
        
        if ((guess.length() > 1) && secretWord.equals(guess)) {
            score++;
            gameRunning = false;
            Message m = new Message(MessageType.GAMEINFO, "", hiddenWord.toString(), secretWord, remainingAttempts, score, gameRunning);
            prepareToSend(m);
        } else if ((guess.length() == 1) && secretWord.contains(guess)) {
            for (int i = 0; i < secretWordLength; i++) {
                if (guess.charAt(0) == secretWord.charAt(i)) {
                    if (i > 0) {
                        hiddenWord = hiddenWord.substring(0, i) + secretWord.charAt(i) + hiddenWord.substring(i + 1);
                    }
                    if (i == 0) {
                        hiddenWord = secretWord.charAt(i) + hiddenWord.substring(1);
                    }
                }
            }
            Message m;
            if (hiddenWord.equals(secretWord)) {
                score++;
                gameRunning = false;
                m = new Message(MessageType.GAMEINFO, "", hiddenWord, secretWord, remainingAttempts, score, gameRunning);
            } else {
                gameRunning = msg.isGameRunning();
                m = new Message(MessageType.GAMEINFO, "", hiddenWord, secretWord, remainingAttempts, score, gameRunning);
            }
            prepareToSend(m);
        } else {
            remainingAttempts--;
            if (remainingAttempts == 0) {
                gameRunning = false;
                score--;
                Message m = new Message(MessageType.GAMEINFO, "", hiddenWord, secretWord, remainingAttempts, score, gameRunning);
                prepareToSend(m);
            } else {
                gameRunning = msg.isGameRunning();
                Message m = new Message(MessageType.GAMEINFO, "", hiddenWord.toString(), secretWord, remainingAttempts, score, gameRunning);
                prepareToSend(m);
            }
        }
    }

    private void disconnectClient() {
        try {
            clientSocket.close();
            consoleOut.println("Disconnecting client " + clientIdentifier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleMessage() throws IOException {
        msgFromClient.clear();
        int readBytes;
        readBytes = clientSocket.read(msgFromClient);
        if (readBytes == -1) {
            throw new IOException("Client has closed the connection.");
        }
        Message msg = extractFromBuffer(msgFromClient);
        synchronized (this) {
            messages.add(msg);
        }

        ForkJoinPool.commonPool().execute(this);
    }

    private Message extractFromBuffer(ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[msgFromClient.remaining()];
        msgFromClient.get(bytes);
        return g.fromJson(new String(bytes), Message.class);
    }

    private void prepareToSend(Message msg) {
        String m = g.toJson(msg);
        ByteBuffer buff = ByteBuffer.wrap(m.getBytes());
        synchronized (this) {
            messagesToSend.add(buff);
        }
        server.readyToSend(clientSocket);
    }

    void sendAllMsg() throws IOException {
        ByteBuffer msg;
        synchronized (messagesToSend) {
            while ((msg = messagesToSend.peek()) != null) {
                sendMessage(msg);
                messagesToSend.remove();
            }
        }
    }

    private void sendMessage(ByteBuffer msg) throws IOException {
        clientSocket.write(msg);
        if (msg.hasRemaining()) {
            throw new MessageException("Could not send message");
        }
    }

    private String pickWord() {
        HangmanWord hangmanWord = new HangmanWord();
        RandomGenerator randomgen = new RandomGenerator();
        int randomWord = randomgen.nextInt(0, (hangmanWord.getWordCount() - 1));
        String pickedWord = hangmanWord.getWord(randomWord);
        return pickedWord;
    }

    private String showNumberOfLetters(String secretWord) {
        String result = "";
        for (int i = 0; i < secretWord.length(); i++) {
            result = result + "_";
        }
        return result;
    }

    void disconnect() throws IOException {
        clientSocket.close();
    }

}
