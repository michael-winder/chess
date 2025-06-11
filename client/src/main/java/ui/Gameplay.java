package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import serverHelp.ServerFacade;
import ui.Websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class Gameplay {

    private String authToken;
    String url;
    public ServerFacade serverFacade;
    WebSocketFacade ws;
    public int gameID;

    public Gameplay(String authToken, String url, WebSocketFacade ws, int gameID){
        this.url = url;
        this.authToken = authToken;
        this.serverFacade = new ServerFacade(url);
        this.ws = ws;
        this.gameID = gameID;
    }

    public String eval(String input){
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
            case "redraw" -> redraw(params);
            case "leave" -> leave(params);
            case "make" -> makeMove(params);
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
        ws.leave(authToken, gameID);
        return "Left game\n";
    }

    public String redraw(String... params){
        if (params.length != 0){
            return "Invalid redraw request. Please simply type: redraw\n";
        }
        return "Redraw request successful!\n";
    }

    public String makeMove(String... params){
        if (params.length != 3 && !Objects.equals(params[0], "move")){
            return "Invalid make move request. Please use the format: make move <STARTING POSITION> <ENDING POSITION>\n";
        }
        ChessPosition startPosition = new ChessPosition(2,2);
        ChessPosition endPosition = new ChessPosition(3, 2);
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        ws.makeMove(authToken, gameID, move);
        return "Success!\n";
    }
}
