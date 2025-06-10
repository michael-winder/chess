package ui;

import chess.ChessBoard;
import chess.ChessGame;
import serverHelp.ServerFacade;
import ui.Websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.HashMap;

public class Gameplay {

    private String authToken;
    public ChessBoard board;
    String url;
    public ServerFacade serverFacade;
    WebSocketFacade ws;
    public String username;
    public int gameID;

    public Gameplay(String authToken, String url, WebSocketFacade ws, String username, int gameID){
        this.url = url;
        this.authToken = authToken;
        this.serverFacade = new ServerFacade(url);
        this.ws = ws;
        this.username = username;
        this.gameID = gameID;
    }

    public String eval(String input){
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
//            case "redraw" -> redraw(params);
            case "leave" -> leave(params);
//            case "make move" -> makeMove(params);
//            case "resign" -> resign(params);
//            case "highlight" -> highlight(params);
            default -> help();
        };
    }

    public String help() {
        return """
                - help - to view possible commands
                - redraw - to redraw the chess board
                - leave to leave your current game
                - make move <STARTING POSITION> <ENDING POSITION> - to move one of your pieces
                - resign - to forfeit your current game
                - highlight <PIECE POSITON> - to see the possible moves for the specified piece
                """;
    }

    public String leave(String... params){
        if (params.length != 0){
            return "Invalid leave request. Please simply type: leave\n";
        }
        ws.leave(authToken, gameID, username);
        return "Left game\n";
    }
}
