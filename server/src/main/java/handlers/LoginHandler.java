package handlers;

import com.google.gson.Gson;
import dataaccess.*;
import exception.BadRequestException;
import org.eclipse.jetty.server.Authentication;
import requests.LoginRequest;
import responses.LoginResponse;
import service.UserService;
import spark.Request;
import spark.Response;

import javax.xml.transform.Result;

public class LoginHandler {
    final UserDAO userAccess;
    final AuthDAO authAccess;
    final GameDAO gameAccess;
    private final UserService userService;
    public LoginHandler(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        userService = new UserService(userAccess, authAccess, gameAccess);
    }

    public String loginUser(Request req, Response res) throws BadRequestException, DataAccessException{
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResponse response = userService.login(loginRequest);
        return new Gson().toJson(response);
    }
}
