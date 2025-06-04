package ui;

import com.sun.nio.sctp.NotificationHandler;

import java.util.Objects;
import java.util.Scanner;

public class Repl{
    Prelogin prelogin = new Prelogin();
    public boolean loginStatus = false;
    String authToken;
    public void run(){
        System.out.println("WELCOME TO THE MOST EPIC CHESS GAME EVER! Type help to get started.");
        Scanner scanner = new Scanner(System.in);
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
                System.out.print(msg);
            }
        }
        Postlogin postlogin = new Postlogin(authToken);
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
                System.out.print(msg);
            }
        }
    }
}
