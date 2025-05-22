package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import requests.JoinRequest;
import responses.JoinResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinHandler {
    final UserDAO userAccess;
    final AuthDAO authAccess;
    final GameDAO gameAccess;
    private final GameService gameService;
    public JoinHandler(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        gameService = new GameService(userAccess, authAccess, gameAccess);
    }

    public String joinGame(Request req, Response res) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        String authToken = req.headers("authorization");
        var joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        JoinResponse response = gameService.join(joinRequest, authToken);
        return new Gson().toJson(response);
    }
}
