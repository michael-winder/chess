package handlers;

import com.google.gson.Gson;
import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import requests.RegisterRequest;
import responses.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler{
    final UserDAO userAccess;
    final AuthDAO authAccess;
    final GameDAO gameAccess;
    private final UserService userService;
    public RegisterHandler(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        userService = new UserService(userAccess, authAccess, gameAccess);
    }


    public String registerUser(Request req, Response res) throws AlreadyTakenException, BadRequestException {
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResponse registerResponse = userService.register(registerRequest);
        return new Gson().toJson(registerResponse);
    }
}
