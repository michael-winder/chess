package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.UnauthorizedException;
import requests.LogoutRequest;
import responses.ListResponse;
import responses.LogoutResponse;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ListHandler {
    final UserDAO userAccess;
    final AuthDAO authAccess;
    final GameDAO gameAccess;
    private final GameService gameService;
    public ListHandler(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        gameService = new GameService(userAccess, authAccess, gameAccess);
    }

    public String listGames(Request req, Response res) throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("authorization");
        ListResponse response = gameService.list(authToken);
        return new Gson().toJson(response);
    }
}
