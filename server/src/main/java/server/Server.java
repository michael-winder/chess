package server;

import exception.BadRequestException;
import handlers.ClearHandler;
import handlers.ExceptionHandler;
import service.UserService;
import spark.*;
import exception.AlreadyTakenException;
import handlers.RegisterHandler;

public class Server {
    private final UserService userService = new UserService();
    private final RegisterHandler regHandler = new RegisterHandler();
    private final ClearHandler clearHandler = new ClearHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user",regHandler::registerUser);
        Spark.delete("/db",clearHandler::clearAll);
        Spark.exception(AlreadyTakenException.class, ExceptionHandler::takenHandler);
        Spark.exception(BadRequestException.class, ExceptionHandler::badRequestHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

//    private void exceptionHandler(AlreadyTakenException ex, Request req, Response res) {
//        res.status(ex.StatusCode());
//        res.body(ex.toJson());
//    }

//    public Object registerUser(Request req, Response res) throws AlreadyTakenException{
//        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
//        RegisterResponse registerResponse = userService.register(registerRequest);
//        return new Gson().toJson(registerResponse);
//    }
}
