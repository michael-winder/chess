package websocket.commands;

import chess.ChessGame;

public class LeaveCommand extends UserGameCommand{

    public String username;

    public LeaveCommand(String authToken, Integer gameID, String username){
        super(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.username = username;
    }
}
