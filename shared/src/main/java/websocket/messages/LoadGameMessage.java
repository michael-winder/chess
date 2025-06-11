package websocket.messages;

import chess.ChessBoard;
import model.GameData;

public class LoadGameMessage extends ServerMessage {

    public GameData gameData;

    public LoadGameMessage(GameData gameData){
        super(ServerMessageType.LOAD_GAME);
        this.gameData = gameData;
    }
}
