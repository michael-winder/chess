package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.sun.nio.sctp.NotificationHandler;
import ui.Websocket.WebSocketFacade;
import websocket.messages.JoinNotification;
import websocket.messages.ServerMessage;

import java.util.Objects;
import java.util.Scanner;

import static java.awt.Color.BLUE;

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
            board = postlogin.currentGame.getBoard();
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
        Gameplay gameplay = new Gameplay(authToken, url, ws, username, gameID);
        if (joinStatus) {
            this.color = color;
            BoardDrawer.drawBoard(board, color);
        }
        String result = "";
        while(!result.equals("Left game\n")){
            String line = scanner.nextLine();
            try {
                result = gameplay.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public void notify(JoinNotification message) {
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
            BoardDrawer.drawBoard(board, color);
        } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.println(message.message);
        } else {
            System.out.println("Error");
        }
    }
}
