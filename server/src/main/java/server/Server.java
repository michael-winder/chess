package server;

import dataaccess.*;
import exception.BadRequestException;
import exception.UnauthorizedException;
import handlers.*;
import handlers.ExceptionHandler;
import service.UserService;
import spark.*;
import exception.AlreadyTakenException;

public class Server {
    private final UserDAO userAccess = new UserMemoryAccess();
    private final AuthDAO authAccess = new AuthMemoryAccess();
    private final GameDAO gameAccess = new GameMemoryAccess();
    private final RegisterHandler regHandler = new RegisterHandler(userAccess, authAccess, gameAccess);
    private final ClearHandler clearHandler = new ClearHandler(userAccess, authAccess, gameAccess);
    private final LoginHandler loginHandler = new LoginHandler(userAccess, authAccess, gameAccess);
    private final LogoutHandler logoutHandler = new LogoutHandler(userAccess, authAccess, gameAccess);
    private final CreateHandler createHandler = new CreateHandler(userAccess, authAccess, gameAccess);
    private final ListHandler listHandler = new ListHandler(userAccess, authAccess, gameAccess);


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", regHandler::registerUser);
        Spark.delete("/db", clearHandler::clearAll);
        Spark.post("/session", loginHandler::loginUser);
        Spark.delete("/session", logoutHandler::logoutUser);
        Spark.post("/game", createHandler::createGame);
        Spark.get("/game", listHandler::listGames);
        Spark.exception(AlreadyTakenException.class, ExceptionHandler::takenHandler);
        Spark.exception(BadRequestException.class, ExceptionHandler::badRequestHandler);
        Spark.exception(UnauthorizedException.class, ExceptionHandler::unauthorizedHandler);
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
