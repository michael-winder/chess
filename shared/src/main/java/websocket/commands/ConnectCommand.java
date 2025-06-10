package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand{

    public boolean join;
    public String username;
    public ChessGame.TeamColor color;

    public ConnectCommand(String authToken, Integer gameID, Boolean join, String username, ChessGame.TeamColor color){
        super(CommandType.CONNECT, authToken, gameID);
        this.join = join;
        this.username = username;
        this.color = color;
    }
}
