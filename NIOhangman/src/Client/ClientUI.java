package Client;

import Common.Message;
import Common.MessageType;
import Common.ThreadSafeStdOut;
import java.util.Scanner;

public class ClientUI implements Runnable {
    private boolean running = false;
    private boolean connected = false;
    private boolean gameRunning = false;
    private static final String PROMT = "> ";
    private final Scanner input = new Scanner(System.in);
    private final ThreadSafeStdOut consoleOut = new ThreadSafeStdOut();
    private final ServerConnection server = new ServerConnection(new ServerMessageOutput());

    public void start(){
        running = true;
        new Thread(this).start();
    }

    public void run() {
        consoleOut.println("To connect to a server, type: connect <host> <port>");
        consoleOut.println("Type QUIT to end the program");

        while(running){
            consoleOut.print(PROMT);
            try{
                CmdLine cmd = new CmdLine(input.nextLine());
                switch(cmd.getCmd()){
                    case CONNECT:
                        if(connected){
                            consoleOut.println("You are already connected to the server!");
                        }
                        else{
                            consoleOut.println("Trying to connect to server...");
                            String host = cmd.getArgs()[0];
                            int port = Integer.valueOf(cmd.getArgs()[1]);
                            server.connect(host,port);
                            }
                        break;
                    case QUIT:
                        if(connected){
                            server.disconnect();
                            connected = false;
                        }
                        else{
                            running = false;
                        }
                        break;
                    case START:
                        if(connected){
                            server.startGame();
                        }
                        else{
                            consoleOut.println("Connect to a server first!");
                            consoleOut.println("To connect to a server, type: connect <host> <port>");
                        }
                        break;
                    case GUESS:
                        if(connected){
                            if(gameRunning){
                                String guess = cmd.getArgs()[0];
                                server.makeGuess(guess);
                            }
                            else{
                                consoleOut.println("Type START to begin a new game!");
                            }
                        }
                        else{
                            consoleOut.println("Connect to a server first!");
                            consoleOut.println("To connect to a server, type: connect <host> <port>");
                        }
                        break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class ServerMessageOutput implements ServerMessageHandler {
        public void handleMsg(Message msg) {
            if(msg.getMsgType() == MessageType.INFO){
                if(msg.isConnectedToServer()){
                    connected = true;
                }
                consoleOut.println(msg.getMessage());
                consoleOut.println("Type START to begin a new game or QUIT to end");
                consoleOut.print(PROMT);
            }
            else if(msg.getMsgType() == MessageType.GAMEINFO){
                gameRunning = msg.isGameRunning();
                if(!msg.getMessage().equals("")){
                    consoleOut.println(msg.getMessage());
                }
                consoleOut.print(msg.getHiddenWord());
                consoleOut.print("\t");
                consoleOut.print("Remaining attempts: " + msg.getRemainingAttempts());
                consoleOut.println(" Score: " + msg.getScore());
                if(!msg.isGameRunning()){
                    consoleOut.println("\n***Game ended.The answer is: "+ msg.getSecretWord() +"\n***Type START for another game or QUIT to end");
                }
                consoleOut.print(PROMT);
            }
        }

        public void disconnected(){
            connected = false;
            consoleOut.println("Disconnected from server.");
            consoleOut.println("To connect to a server, type: connect <host> <port>");
            consoleOut.println("Type QUIT to end the program");
            consoleOut.print(PROMT);
        }
    }
}
