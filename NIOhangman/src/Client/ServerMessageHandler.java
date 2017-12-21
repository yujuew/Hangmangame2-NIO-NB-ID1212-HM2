package Client;

import Common.Message;

public interface ServerMessageHandler {
    void handleMsg(Message msg);
    void disconnected();
}
