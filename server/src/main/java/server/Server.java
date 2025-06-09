package server;

import dataaccess.*;
import exception.BadRequestException;
import exception.ResponseException;
import exception.UnauthorizedException;
import handlers.*;
import handlers.ExceptionHandler;
import spark.*;
import exception.AlreadyTakenException;
import websocket.WebSocketHandler;

public class Server {
    private final UserDAO userAccess;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    private final RegisterHandler regHandler;
    private final ClearHandler clearHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final CreateHandler createHandler;
    private final ListHandler listHandler;
    private final JoinHandler joinHandler;
    private final WebSocketHandler webSocketHandler;
    public Server(){
        try {
            this.userAccess = new UserSQLAccess();
            this.authAccess = new AuthSQLAccess();
            this.gameAccess = new GameSQLAccess();
        } catch (DataAccessException e){
            throw new ResponseException(500, "unable to initialize Database");
        }
        this.regHandler = new RegisterHandler(userAccess, authAccess, gameAccess);
        this.clearHandler = new ClearHandler(userAccess, authAccess, gameAccess);
        this.loginHandler = new LoginHandler(userAccess, authAccess, gameAccess);
        this.logoutHandler = new LogoutHandler(userAccess, authAccess, gameAccess);
        this.createHandler = new CreateHandler(userAccess, authAccess, gameAccess);
        this.listHandler = new ListHandler(userAccess, authAccess, gameAccess);
        this.joinHandler = new JoinHandler(userAccess, authAccess, gameAccess);
        this.webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", webSocketHandler);
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", regHandler::registerUser);
        Spark.delete("/db", clearHandler::clearAll);
        Spark.post("/session", loginHandler::loginUser);
        Spark.delete("/session", logoutHandler::logoutUser);
        Spark.post("/game", createHandler::createGame);
        Spark.get("/game", listHandler::listGames);
        Spark.put("/game", joinHandler::joinGame);
        Spark.exception(AlreadyTakenException.class, ExceptionHandler::takenHandler);
        Spark.exception(BadRequestException.class, ExceptionHandler::badRequestHandler);
        Spark.exception(UnauthorizedException.class, ExceptionHandler::unauthorizedHandler);
        Spark.exception(DataAccessException.class, ExceptionHandler::dataAccessHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
