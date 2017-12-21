/*
 * To listen to the client
 */
package Server;

import Common.MessageException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;


public class HangmanServer {

    private static final int LINGER_TIME = 6000;
    private int portNumber = 3300;
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;

    public void serve() {
        try {
            selector = Selector.open();
            listeningSocketChannel = ServerSocketChannel.open();
            listeningSocketChannel.configureBlocking(false);
            listeningSocketChannel.bind(new InetSocketAddress(portNumber));
            listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if (selector.select() <= 0) {
                    continue;
                }
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        createHandler(key);
                    } else if (key.isReadable()) {
                        recvMessage(key);
                    } else if (key.isWritable()) {
                        sendMessage(key);
                    }
                }
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private void createHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel clientSocket = serverSocket.accept();
        clientSocket.configureBlocking(false);
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientSocket.register(selector, SelectionKey.OP_READ, clientHandler);
        clientSocket.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);

    }


    private void recvMessage(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        try {
            client.handleMessage();
        } catch (IOException ioe) {
            disconnectClient(key);
        }
    }

    private void sendMessage(SelectionKey key) {
        ClientHandler client = (ClientHandler) key.attachment();
        try {
            client.sendAllMsg();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void disconnectClient(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        client.disconnect();
        key.cancel();
    }

    void readyToSend(SocketChannel clientSocket) {
        clientSocket.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    public static void main(String[] args) {
        HangmanServer server = new HangmanServer();
        server.serve();
    }
}
