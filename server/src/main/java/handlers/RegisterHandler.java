package handlers;

import com.google.gson.Gson;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import requests.RegisterRequest;
import responses.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler{
    private final UserService userService = new UserService();


    public String registerUser(Request req, Response res) throws AlreadyTakenException, BadRequestException {
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResponse registerResponse = userService.register(registerRequest);
        return new Gson().toJson(registerResponse);
    }
}
