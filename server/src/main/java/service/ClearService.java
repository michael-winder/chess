package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO userAccess ;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    public ClearService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }


    public String clear() throws DataAccessException{
        userAccess.deleteAllUsers();
        authAccess.deleteAllAuth();
        gameAccess.deleteAllGames();
        return "{}";
    }


}
