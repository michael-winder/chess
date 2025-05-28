package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.BadRequestException;
import exception.UnauthorizedException;
import requests.CreateRequest;
import responses.CreateResponse;
import service.GameService;
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

    public String createGame(Request req, Response res) throws UnauthorizedException, BadRequestException, DataAccessException {
        String authToken = req.headers("authorization");
        var createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        CreateResponse response = gameService.create(createRequest, authToken);
        return new Gson().toJson(response);
    }
}
