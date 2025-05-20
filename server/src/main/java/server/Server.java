package server;

import com.google.gson.Gson;
import model.RegisterRequest;
import model.RegisterResponse;
import service.UserService;
import spark.*;
import exception.AlreadyTakenException

public class Server {
    private final UserService userService;

    public Server(UserService userService){
        this.userService = userService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user",this::registerUser);
        Spark.exception(AlreadyTakenException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(AlreadyTakenException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    public Object registerUser(Request req, Response res){
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResponse registerResponse = userService.register(registerRequest);
        return new Gson().toJson(registerResponse);
    }
}
