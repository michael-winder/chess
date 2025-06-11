package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import ui.Websocket.WebSocketFacade;
import websocket.messages.ErrorMesage;
import websocket.messages.NotificationMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.Objects;
import java.util.Scanner;

public class Repl implements ui.Websocket.NotificationHandler {
    Prelogin prelogin;
    public boolean loginStatus = false;
    public boolean joinStatus = false;
    public boolean quitStatus = false;
    public ChessGame.TeamColor color;
    public ChessBoard board;
    public String username;
    String authToken;
    String url;
    WebSocketFacade ws;
    public GameData currentGame;
    public boolean gameOver;

    public Repl (String url){
        this.url = url;
        this.prelogin = new Prelogin(url);
    }

    public void run(){
        System.out.println("WELCOME TO THE MOST EPIC CHESS GAME EVER! Type help to get started.");
        Scanner scanner = new Scanner(System.in);
        while (!quitStatus) {
            preLoginUI(scanner);
            Postlogin postlogin = new Postlogin(authToken, url, this, username);
            postLoginUI(scanner, postlogin);
            currentGame = postlogin.currentGame;
            board = postlogin.currentGame.game().getBoard();
            gameplayUI(scanner, postlogin.globalColor, postlogin.gameID);
        }
    }

    private void preLoginUI(Scanner scanner){
        var result = " ";
        while(!result.equals("quit") && !loginStatus){
            String line = scanner.nextLine();
            try {
                result = prelogin.eval(line);
                System.out.print(result);
                authToken = prelogin.authToken;
                if (Objects.equals(result, "Logged in!\n") || Objects.equals(result, "Registered!\n")){
                    loginStatus = true;
                    username = prelogin.username;
                }
            } catch (Throwable e){
                var msg = e.toString();
                if (Objects.equals(msg, "exception.ResponseException: Error: already taken")){
                    System.out.print("That username is already taken. Try a different one!\n");
                } else {
                    System.out.print("Invalid login credentials! Try again:\n");
                }
            }
        }
        if (result.equals("quit")){
            quitStatus = true;
        }
    }

    private void postLoginUI(Scanner scanner, Postlogin postlogin){
        String result = "";
        while(!result.equals("quit") && loginStatus){
            String line = scanner.nextLine();
            try {
                result = postlogin.eval(line);
                System.out.print(result);
                if (Objects.equals(result, "Logged out!\n")){
                    loginStatus = false;
                    joinStatus = false;
                }
                if (Objects.equals(result, "Joined!\n") || Objects.equals(result, "Observing!\n")){
                    joinStatus = true;
                    ws = postlogin.ws;
                    color = postlogin.globalColor;
                    break;
                }
            } catch (Throwable e){
                var msg = e.toString();
                if (Objects.equals(msg, "exception.ResponseException: Error: bad request") ||
                        e instanceof NumberFormatException){
                    System.out.print("That is not a valid game number. Please type a valid game game number\n");
                } else if (Objects.equals(msg, "exception.ResponseException: Error: already taken")){
                    System.out.print("That color is already taken!\n");
                } else {
                    System.out.print(msg);
                }
            }
        }
        if (result.equals("quit")){
            quitStatus = true;
        }
    }

    private void gameplayUI(Scanner scanner, ChessGame.TeamColor color, int gameID){
        Gameplay gameplay = new Gameplay(authToken, url, ws, gameID);
        String result = "";
        while(!result.equals("Left game\n") && joinStatus){
            String line = scanner.nextLine();
            try {
                result = gameplay.eval(line);
                System.out.print(result);
                if (Objects.equals(result, "Redraw request successful!\n")){
                    BoardDrawer.drawBoard(board, color);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public void notify(ServerMessage.ServerMessageType type, String message) {
        if (type == ServerMessage.ServerMessageType.LOAD_GAME){
            LoadGameMessage gameMessage = new Gson().fromJson(message, LoadGameMessage.class);
            BoardDrawer.drawBoard(gameMessage.game.game().getBoard(), color);
        } else if (type == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage joinMessage = new Gson().fromJson(message, NotificationMessage.class);
            System.out.println(joinMessage.message);
            if (Objects.equals(message, "GAME OVER!")){
                joinStatus = false;
            }
        } else {
            ErrorMesage errorMesage = new Gson().fromJson(message, ErrorMesage.class);
            System.out.println(errorMesage.errorMessage);
        }
    }
}
