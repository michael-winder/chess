package ui;

import chess.ChessGame;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import responses.ListResponse;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;

public class Postlogin {
    private final String authToken;
    public Postlogin (String authToken){
        this.authToken = authToken;
    }

    public final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
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
        if(params.length >= 1){
            return "Invalid input. Please type one of the following commands:" + response;
        }
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
        for(GameData game : response.games()){
            gameString.append(game.gameName()).append("\n");
        }
        return gameString.toString();
    }

    public String join(String... params){
        if (params.length != 2){
            return "Invalid join game request. Please use the format: join <GAME ID> [WHITE|BLACK]\n";
        }
        ChessGame.TeamColor color;
        if (Objects.equals(params[1], "white")){
            color = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(params[1], "black")){
            color = ChessGame.TeamColor.BLACK;
        } else {
            return "Invalid color";
        }
        JoinRequest request = new JoinRequest(color, Integer.parseInt(params[0]));
        serverFacade.joinGame(request, authToken);
        return "Joined!\n";
    }

    public String observe(String... params){
        if (params.length != 1){
            return "Invalid observe game request. Please use the format: observe <GAME ID>\n";
        }
        return "Observing!\n";
    }
}
