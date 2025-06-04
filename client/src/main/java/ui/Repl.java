package ui;

import chess.ChessBoard;
import com.sun.nio.sctp.NotificationHandler;

import java.util.Objects;
import java.util.Scanner;

public class Repl{
    Prelogin prelogin = new Prelogin();
    Gameplay gameplay = new Gameplay();
    public boolean loginStatus = false;
    String authToken;
    public void run(){
        System.out.println("WELCOME TO THE MOST EPIC CHESS GAME EVER! Type help to get started.");
        Scanner scanner = new Scanner(System.in);
        preLoginUI(scanner);
        Postlogin postlogin = new Postlogin(authToken);
        postLoginUI(scanner, postlogin);
    }

    private void preLoginUI(Scanner scanner){
        var result = " ";
        while(!result.equals("quit") && !loginStatus){
            String line = scanner.nextLine();
            try {
                result = prelogin.eval(line);
                System.out.print(result);
                authToken = prelogin.authToken;
                if (Objects.equals(result, "Logged in!\n")){
                    loginStatus = true;
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
                }
            } catch (Throwable e){
                var msg = e.toString();
                if (Objects.equals(msg, "exception.ResponseException: Error: bad request") ||
                        e instanceof NumberFormatException){
                    System.out.print("That is not a valid game ID. Please type a valid game ID\n");
                } else if (Objects.equals(msg, "exception.ResponseException: Error: already taken")){
                    System.out.print("That color is already taken!\n");
                } else {
                    System.out.print(msg);
                }
            }
        }
    }

    private void gameplayUI(Scanner scanner){
        ChessBoard board = new ChessBoard();
        Gameplay.drawBoard(board);
    }
}
