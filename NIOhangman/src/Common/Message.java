package Common;

 public class Message{
    private MessageType msgType;
    private String message;
    private String hiddenWord;
    private String secretWord;
    private int remainingAttempts;
    private int score = 0;
    private boolean gameRunning;
    private boolean connectedToServer;

    public Message(){
    }
    public Message(MessageType msgType) {
        this.msgType = msgType;
    }

    public Message(MessageType msgType, String message) {
        this.msgType = msgType;
        this.message = message;
    }

    public Message(MessageType msgType, String message, boolean connectedToServer) {
        this.msgType = msgType;
        this.message = message;
        this.connectedToServer = connectedToServer;
    }

    public Message(MessageType msgType, int score, boolean connectedToServer) {
        this.msgType = msgType;
        this.score = score;
        this.connectedToServer = connectedToServer;
    }

    public Message(MessageType msgType, String message, String currentWord, String correctWord, int remainingAttempts, int score, boolean gameRunning) {
        this.msgType = msgType;
        this.message = message;
        this.hiddenWord = currentWord;
        this.secretWord = correctWord;
        this.remainingAttempts = remainingAttempts;
        this.score = score;
        this.gameRunning = gameRunning;
    }

    public Message(MessageType msgType, String message, String correctWord, int remainingAttempts, int score, boolean gameRunning) {
        this.msgType = msgType;
        this.message = message;
        this.secretWord = correctWord;
        this.remainingAttempts = remainingAttempts;
        this.score = score;
        this.gameRunning = gameRunning;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public String getMessage() {
        return message;
    }

    public String getSecretWord() {
        return secretWord;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public boolean isConnectedToServer() {
        return connectedToServer;
    }

    public String getHiddenWord() {
        return hiddenWord;
    }
}