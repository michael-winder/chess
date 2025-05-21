package service;

import dataaccess.*;
import responses.ClearResponse;

public class ClearService {
    private final UserDAO userAccess = new UserMemoryAccess();
    private final AuthDAO authAccess = new AuthMemoryAccess();
    private final GameDAO gameAccess = new GameMemoryAccess();


    public ClearResponse clear(){
        userAccess.deleteAllUsers();
        authAccess.deleteAllAuth();
        gameAccess.deleteAllGames();
        return new ClearResponse();
    }
}
