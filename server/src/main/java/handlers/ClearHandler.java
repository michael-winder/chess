package handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.ClearService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    final UserDAO userAccess;
    final AuthDAO authAccess;
    final GameDAO gameAccess;
    private final ClearService clearService;
    public ClearHandler(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        clearService = new ClearService(userAccess, authAccess, gameAccess);
    }

    public Object clearAll(Request req, Response res){
        clearService.clear();
        return "{}";
    }
}
