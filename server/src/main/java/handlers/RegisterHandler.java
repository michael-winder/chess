package handlers;

import com.google.gson.Gson;
import exception.AlreadyTakenException;
import model.RegisterRequest;
import model.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler{
    private final UserService userService = new UserService();


    public RegisterResponse registerUser(Request req, Response res) throws AlreadyTakenException{
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResponse registerResponse = userService.register(registerRequest);
        return new Gson().toJson(registerResponse);
    }
}
