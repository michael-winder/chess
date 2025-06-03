package ui;

import com.sun.nio.sctp.NotificationHandler;

import java.util.Objects;
import java.util.Scanner;

public class Repl{
    Prelogin prelogin = new Prelogin();
    public boolean loginStatus = false;
    public void run(){
        System.out.println("WELCOME TO THE MOST EPIC CHESS GAME EVER! Type help to get started.");
        Scanner scanner = new Scanner(System.in);
        var result = " ";
        while(!result.equals("quit") && !loginStatus){
            String line = scanner.nextLine();
            try {
                result = prelogin.eval(line);
                System.out.print(result);
                if (Objects.equals(result, "Logged in!\n")){
                    loginStatus = true;
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }
}
