package websocket.messages;

import websocket.commands.UserGameCommand;

public class JoinNotification extends ServerMessage{

    public String message;

    public JoinNotification(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
