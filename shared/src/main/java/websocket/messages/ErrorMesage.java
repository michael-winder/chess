package websocket.messages;

import model.GameData;

public class ErrorMesage extends ServerMessage{

    public String errorMessage;

    public ErrorMesage (String errorMessage){
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}
