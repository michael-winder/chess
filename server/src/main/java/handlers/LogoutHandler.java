package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.BadRequestException;
import exception.UnauthorizedException;
import requests.LoginRequest;
import requests.LogoutRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    final UserDAO userAccess;
    final AuthDAO authAccess;
    final GameDAO gameAccess;
    private final UserService userService;
    public LogoutHandler(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        userService = new UserService(userAccess, authAccess, gameAccess);
    }


    public String logoutUser(Request req, Response res) throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("authorization");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResponse response = userService.logout(logoutRequest);
        return new Gson().toJson(response);
    }
}
