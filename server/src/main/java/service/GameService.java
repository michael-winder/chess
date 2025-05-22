package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requests.CreateRequest;
import responses.CreateResponse;

public class GameService {
    private final UserDAO userAccess ;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    public GameService (UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public CreateResponse create(CreateRequest request){
        int gameID = gameAccess.createGame(null,null, request.gameName(), new ChessGame());
        return new CreateResponse(gameID);
    }


}

