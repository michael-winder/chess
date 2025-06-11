package websocket.messages;

import chess.ChessBoard;
import model.GameData;

public class LoadGameMessage extends ServerMessage {

    public GameData game;

    public LoadGameMessage(GameData game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
