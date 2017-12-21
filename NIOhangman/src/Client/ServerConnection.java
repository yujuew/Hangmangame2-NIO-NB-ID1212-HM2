package Client;

import com.google.gson.Gson;
import Common.Message;
import Common.MessageType;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

public class ServerConnection implements Runnable{
    private static final int MAX_MESSAGE_SIZE = 512;
    private volatile boolean connected;
    private volatile boolean timeToSend = false;
    private final ByteBuffer msgFromServer = ByteBuffer.allocateDirect(MAX_MESSAGE_SIZE);
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();
    private final Gson g = new Gson();
    private SocketChannel socket;
    private Message msg = null;
    private InetSocketAddress serverAddress;
    Selector selector;
    ServerMessageHandler messageHandler;

    public ServerConnection(ServerMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try{
            initSocket();
            initSelector();

            while(connected || !messagesToSend.isEmpty()){
                if(timeToSend){
                    socket.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }

                selector.select();
                for(SelectionKey key : selector.selectedKeys()){
                    selector.selectedKeys().remove(key);
                    if(!key.isValid()){
                        continue;
                    }
                    if(key.isConnectable()){
                        completeConnection(key);
                    }
                    else if(key.isReadable()){
                        recvMessage();
                    }
                    else if(key.isWritable()){
                        sendMessage(key);
                    }
                }
            }
        }
        catch(IOException ioe){}
        try{
            forceDisconnect();
        }
        catch(IOException ioe){
        }
    }

    private void initSocket() throws IOException {
        socket = SocketChannel.open();
        socket.configureBlocking(false);
        socket.connect(serverAddress);
        connected = true;
    }

    private void initSelector() throws IOException {
        selector = Selector.open();
        socket.register(selector,SelectionKey.OP_CONNECT);
    }

    private void completeConnection(SelectionKey key) throws IOException{
        socket.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try{
            InetSocketAddress remoteAddr = (InetSocketAddress) socket.getRemoteAddress();
            messageHandler.handleMsg(new Message(MessageType.INFO,"Connected to " + remoteAddr.getAddress().getHostAddress() + ":" + remoteAddr.getPort(),true));
        }
        catch(IOException ioe){
            messageHandler.handleMsg(new Message(MessageType.INFO,"Connected to " + serverAddress.getAddress().getHostAddress() + ":" + serverAddress.getPort(),true));
        }
    }

    public void connect(String host, int port) throws IOException {
        serverAddress = new InetSocketAddress(host,port);
        new Thread(this).start();
    }

    public void disconnect() throws IOException {
        Message m = new Message(MessageType.QUIT);
        prepareToSend(m);
        synchronized (this){
            connected = false;
        }
    }

    private void forceDisconnect() throws IOException {
        socket.close();
        socket.keyFor(selector).cancel();
        synchronized (this){
            connected = false;
        }
        messageHandler.disconnected();
    }

    private void recvMessage() throws IOException{
        msgFromServer.clear();
        int count = socket.read(msgFromServer);
        if(count == -1){
            throw new IOException("Lost connection to server");
        }
        msg = extractFromBuffer(msgFromServer);
        messageHandler.handleMsg(msg);
    }

    private void sendMessage(SelectionKey key) throws IOException {
        ByteBuffer buff;
        synchronized (messagesToSend){
            while((buff = messagesToSend.peek()) != null){
                socket.write(buff);
                if(buff.hasRemaining()){
                    return;
                }
                messagesToSend.remove();
            }
            if(connected){
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    public void startGame(){
        if(msg !=null){
            msg = new Message(MessageType.START,msg.getScore(),connected);
        }
        else{
            msg = new Message(MessageType.START,0,connected);
        }
        prepareToSend(msg);
    }

    public void makeGuess(String guess){
        if(msg != null){
            msg = new Message(MessageType.GUESS, guess,msg.getHiddenWord(),msg.getSecretWord(),msg.getRemainingAttempts(),msg.getScore(),msg.isGameRunning());
            prepareToSend(msg);
        }
    }

    private Message extractFromBuffer(ByteBuffer buffer){
        buffer.flip();
        byte[] bytes = new byte[msgFromServer.remaining()];
        msgFromServer.get(bytes);
        return g.fromJson(new String(bytes), Message.class);
    }

    private void prepareToSend(Message msg){
        String m = g.toJson(msg);
        ByteBuffer buff = ByteBuffer.wrap(m.getBytes());
        synchronized (messagesToSend){
            messagesToSend.add(buff);
        }
        timeToSend = true;
        selector.wakeup();
    }
}
