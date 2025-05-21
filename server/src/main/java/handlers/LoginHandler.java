package handlers;

import com.google.gson.Gson;
import exception.BadRequestException;
import requests.LoginRequest;
import responses.LoginResponse;
import service.UserService;
import spark.Request;
import spark.Response;

import javax.xml.transform.Result;

public class LoginHandler {
    private final UserService userService = new UserService();

    public String loginUser(Request req, Response res) throws BadRequestException{
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResponse response = userService.login(loginRequest);
        return new Gson().toJson(response);
    }
}
