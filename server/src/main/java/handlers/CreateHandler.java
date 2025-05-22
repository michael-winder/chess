package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.BadRequestException;
import exception.UnauthorizedException;
import requests.CreateRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import responses.CreateResponse;
import responses.LogoutResponse;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class CreateHandler {
    final UserDAO userAccess;
    final AuthDAO authAccess;
    final GameDAO gameAccess;
    private final GameService gameService;
    public CreateHandler(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        gameService = new GameService(userAccess, authAccess, gameAccess);
    }

    public String createGame(Request req, Response res) throws UnauthorizedException, BadRequestException {
        String authToken = req.headers("authorization");
        var createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        CreateResponse response = gameService.create(createRequest, authToken);
        return new Gson().toJson(response);
    }
}
