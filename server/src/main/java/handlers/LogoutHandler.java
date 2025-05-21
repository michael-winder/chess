package handlers;

import com.google.gson.Gson;
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
    private final UserService userService = new UserService();

    public String logoutUser(Request req, Response res) throws UnauthorizedException {
        var logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class);
        LogoutResponse response = userService.logout(logoutRequest);
        return new Gson().toJson(response);
    }
}
