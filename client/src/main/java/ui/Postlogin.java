package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import responses.JoinResponse;
import responses.ListResponse;
import server.ServerFacade;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Postlogin {
    private final String authToken;
    public ChessGame.TeamColor globalColor = null;
    public ChessBoard board;
    String url;
    public final ServerFacade serverFacade;
    private HashMap <Integer, Integer> gameIDs = new HashMap<Integer, Integer>();

    public Postlogin(String authToken, String url){
        this.url = url;
        this.authToken = authToken;
        this.serverFacade = new ServerFacade(url);
    }

    public String eval(String input){
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
            case "logout" -> logout(params);
            case "create" -> create(params);
            case "list" -> list(params);
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "quit" -> "quit";
            default -> help(params);
        };
    }

    public String help(String... params){
        String response = """
                create <GAME NAME> - to create a new game
                list - to list all chess games
                join <GAME ID> [WHITE|BLACK] - to join a game
                observe <GAME ID> - to observe a game
                logout - when you are done
                quit - to stop playing chess
                help - to see possible commands
                """;
        return response;
    }

    public String logout(String... params){
        if (params.length != 0){
            return "Invalid logout request. Please simply type: logout\n";
        }
        serverFacade.logout(authToken);
        return "Logged out!\n";
    }

    public String create(String... params){
        if (params.length != 1){
            return "Invalid create game request. Please use the format: create <GAME NAME>\n";
        }
        CreateRequest request = new CreateRequest(params[0]);
        serverFacade.createGame(request, authToken);
        return "Game created\n";
    }

    public String list(String... params){
        if (params.length != 0){
            return "Invalid list request. Please simply type: list\n";
        }
        ListResponse response = serverFacade.listGames(authToken);
        StringBuilder gameString = new StringBuilder();
        gameString.append("GAME NUMBER:    GAME NAME:      WHITE USERNAME:     BLACK USERNAME:\n");
        int i = 1;
        for(GameData game : response.games()){
            gameString.append(i).append("               ").append(game.gameName());
            gameString.append("           ").append(game.whiteUsername());
            gameString.append("             ").append(game.blackUsername()).append("\n");
            gameIDs.put(i, game.gameID());
            i++;
        }
        return gameString.toString();
    }

    public String join(String... params){
        if (params.length != 2){
            return "Invalid join game request. Please use the format: join <GAME NUMBER> [WHITE|BLACK]\n";
        }
        ChessGame.TeamColor color;
        if (Objects.equals(params[1], "white")){
            color = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(params[1], "black")){
            color = ChessGame.TeamColor.BLACK;
        } else {
            return "Invalid color\n";
        }
        list(new String[0]);
        globalColor = color;
        JoinRequest request = new JoinRequest(color, gameIDs.get(Integer.parseInt(params[0])));
        serverFacade.joinGame(request, authToken);
        return "Joined!\n";
    }

    public String observe(String... params){
        if (params.length != 1){
            return "Invalid observe game request. Please use the format: observe <GAME NUMBER>\n";
        }
        list(new String[0]);
        JoinRequest request = new JoinRequest(null, gameIDs.get(Integer.parseInt(params[0])));
        serverFacade.joinGame(request, authToken);
        return "Observing!\n";
    }
}
