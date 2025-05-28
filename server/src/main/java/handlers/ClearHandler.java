package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import responses.ClearResponse;
import service.ClearService;
import spark.Request;
import spark.Response;
import java.util.ArrayList;

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

    public Object clearAll(Request req, Response res) throws DataAccessException {
        clearService.clear();
        ClearResponse response = new ClearResponse(new ArrayList<>());
        return new Gson().toJson(response);
    }
}
