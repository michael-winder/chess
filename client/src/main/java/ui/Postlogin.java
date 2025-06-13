package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import responses.ListResponse;
import server.help.ServerFacade;
import ui.websocket.WebSocketFacade;
import ui.websocket.NotificationHandler;

import java.util.*;

public class Postlogin {
    public String authToken;
    public ChessGame.TeamColor globalColor = null;
    String url;
    public ServerFacade serverFacade;
    public NotificationHandler notificationHandler;
    private final HashMap <Integer, Integer> gameIDs = new HashMap<Integer, Integer>();
    public WebSocketFacade ws;
    public HashMap<Integer, GameData> gameList = new HashMap<Integer, GameData>();
    public GameData currentGame = new GameData(1, "not", "not", "not", new ChessGame());
    public String username;
    public int gameID;
    public Collection<GameData> gameData;

    public Postlogin(String authToken, String url, NotificationHandler notificationHandler, String username){
        this.url = url;
        this.authToken = authToken;
        this.serverFacade = new ServerFacade(url);
        this.notificationHandler = notificationHandler;
        this.username = username;
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
        gameData = response.games();
        for (GameData game : gameData){
            gameList.put(game.gameID(), game);
        }
        StringBuilder gameString = new StringBuilder();
        gameString.append("GAME NUMBER:        GAME NAME:          WHITE USERNAME:     BLACK USERNAME:\n");
        Integer i = 1;
        for(GameData game : response.games()){
            String gameNum = padRight(i.toString());
            String gameName = padRight(game.gameName());
            String whiteUsername = padRight(game.whiteUsername());
            String blackUsername = padRight(game.blackUsername());
            gameString.append(gameNum).append(gameName).append(whiteUsername).append(blackUsername).append("\n");
            gameIDs.put(i, game.gameID());
            i++;
        }
        return gameString.toString();
    }

    private String padRight(String string){
        if (string == null){
            string = " ";
        }
        StringBuilder stringBuilder = new StringBuilder(string);
        while(stringBuilder.length() < 20){
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
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
        if (!gameIDs.containsKey(Integer.parseInt(params[0]))){
            return "Invalid game\n";
        }
        gameID = gameIDs.get(Integer.parseInt(params[0]));
        JoinRequest request = new JoinRequest(color, gameID);
        serverFacade.joinGame(request, authToken);
        currentGame = getGame(gameID);
        ws = new WebSocketFacade(url, notificationHandler);
        ws.connect(authToken, gameID);
        return "Joined!\n";
    }

    public String observe(String... params){
        if (params.length != 1){
            return "Invalid observe game request. Please use the format: observe <GAME NUMBER>\n";
        }
        list(new String[0]);
        if (!gameIDs.containsKey(Integer.parseInt(params[0]))){
            return "That game does not exist. Please try again with a valid game\n";
        }
        gameID = gameIDs.get(Integer.parseInt(params[0]));
        currentGame = getGame(gameID);
        ws = new WebSocketFacade(url, notificationHandler);
        ws.connect(authToken, gameID);
        return "Observing!\n";
    }

    public GameData getGame(int gameID){
        return gameList.get(gameID);
    }

    public void updateAuth(String authToken){
        this.authToken = authToken;
    }
}
