package ui;

import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.RegisterResponse;
import server_help.ServerFacade;

import java.util.Arrays;

public class Prelogin {
    public ServerFacade serverFacade;
    public String authToken = null;
    public String url;
    public String username;

    public Prelogin(String url){
        this.url = url;
        serverFacade = new ServerFacade(url);
    }

    public String eval(String input){
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help(params);
         };
    }

    public String help(String... params){
        String response = """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - to stop playing chess
                help - to see possible commands
                """;
        if(params.length >= 1){
            return "Invalid input. Please type one of the following commands:\n" + response;
        }
        return response;
    }

    public String register(String... params){
        if (params.length != 3){
            return "Invalid registration. Use format: register <USERNAME> <PASSWORD> <EMAIL>\n";
        }
        username = params[0];
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        RegisterResponse response = serverFacade.register(request);
        authToken = response.authToken();
        return "Registered!\n";
    }

    public String login(String... params){
        if (params.length != 2){
            return "Invalid login request. Use format: login <USERNAME> <PASSWORD>\n";
        }
        username = params[0];
        LoginRequest request = new LoginRequest(params[0], params[1]);
        LoginResponse response = serverFacade.login(request);
        authToken = response.authToken();
        return "Logged in!\n";
    }


}
