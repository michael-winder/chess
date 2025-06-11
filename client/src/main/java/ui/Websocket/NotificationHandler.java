package ui.Websocket;

import websocket.messages.JoinNotification;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage.ServerMessageType type, String message);
}