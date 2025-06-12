package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import server.help.ServerFacade;
import ui.websocket.WebSocketFacade;

import java.util.*;

public class Gameplay {

    private String authToken;
    String url;
    public ServerFacade serverFacade;
    WebSocketFacade ws;
    public int gameID;
    public GameData gameData;
    public ChessGame.TeamColor color;
    Repl repl;

    public Gameplay(String authToken, String url, WebSocketFacade ws, int gameID, GameData gameData, ChessGame.TeamColor color, Repl repl){
        this.url = url;
        this.authToken = authToken;
        this.serverFacade = new ServerFacade(url);
        this.ws = ws;
        this.gameID = gameID;
        this.gameData = gameData;
        this.color = color;
        this.repl = repl;
    }

    public String eval(String input){
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
            case "redraw" -> redraw(params);
            case "leave" -> leave(params);
            case "move" -> makeMove(params);
            case "resign" -> resign(params);
            case "highlight" -> highlight(params);
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
        try {
            if (params.length != 0){
                return "Invalid leave request. Please simply type: leave\n";
            }
            ws.leave(authToken, gameID);
            return "Left game\n";
        } catch (IllegalStateException e) {
            return "Left game\n";
        }
    }

    public String redraw(String... params){
        if (params.length != 0){
            return "Invalid redraw request. Please simply type: redraw\n";
        }
        return "Redraw request successful!\n";
    }

    public String makeMove(String... params){
        if (params.length != 2){
            return "Invalid make move request. Please use the format: move <STARTING POSITION> <ENDING POSITION>\n";
        }
        ChessPosition startPosition = moveCreator(params[0]);
        ChessPosition endPosition = moveCreator(params[1]);
        if (startPosition.getRow() < 1 || startPosition.getRow() > 8 || startPosition.getColumn() == 9
                || endPosition.getRow() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() == 9){
            return "Invalid move. Please try again\n";
        }
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        ws.makeMove(authToken, gameID, move);
        return "Move:\n";
    }

    public String resign(String... params){
        if (params.length != 0){
            return "Invalid resign request. Please simply type: resign\n";
        }
        System.out.print("Are you sure you want to resign? Type yes to continue or anything else to cancel\n");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        if (Objects.equals(line, "yes")){
            ws.resign(authToken, gameID);
            return "Resigned\n";
        } else {
            return "Good, I knew you weren't a quitter\n";
        }
    }

    public String highlight(String... params){
        if (params.length != 1){
            return "Invalid leave request. Please simply type: leave\n";
        }
        GameData gameData = repl.currentGame;
        ChessPosition position = moveCreator(params[0]);
        Collection<ChessMove> possibleMoves = gameData.game().validMoves(position);
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        ChessPosition startPosition = new ChessPosition(9, 9);
        for (ChessMove move : possibleMoves){
            endPositions.add(move.getEndPosition());
            startPosition = move.getStartPosition();
        }
        BoardDrawer.drawPossibleMoves(gameData.game().getBoard(), color, endPositions, startPosition);
        return "Possible moves from position " + params[0] + "\n";
    }

    private ChessPosition moveCreator(String moveString){
        char colChar = moveString.charAt(0);
        int row = Character.getNumericValue(moveString.charAt(1));
        int col;
        if (colChar == 'h'){
            col = 8;
        } else if (colChar == 'g'){
            col = 7;
        } else if (colChar == 'f'){
            col = 6;
        } else if (colChar == 'e'){
            col = 5;
        } else if (colChar == 'd'){
            col = 4;
        } else if (colChar == 'c'){
            col = 3;
        } else if (colChar == 'b'){
            col = 2;
        } else if (colChar == 'a'){
            col = 1;
        } else {
            col = 9;
        }
        return new ChessPosition(row, col);
    }
}
