package service;

import dataaccess.*;
import responses.ClearResponse;

public class ClearService {
    private final UserDAO userAccess ;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    public ClearService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }


    public ClearResponse clear(){
        userAccess.deleteAllUsers();
        authAccess.deleteAllAuth();
        gameAccess.deleteAllGames();
        return new ClearResponse();
    }
}
