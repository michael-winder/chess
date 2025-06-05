package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.AuthData;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import responses.CreateResponse;
import responses.JoinResponse;
import responses.ListResponse;

public class GameService {
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    public GameService (UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public CreateResponse create(CreateRequest request, String authToken) throws DataAccessException {
        if (request.gameName() == null){
            throw new BadRequestException(400,"Error: bad request");
        }
        if (authAccess.getAuth(authToken) == null){
            throw new UnauthorizedException(401, "Error: unauthorized");
        }
        int gameID = gameAccess.createGame(null,null, request.gameName(), new ChessGame());
        return new CreateResponse(gameID);
    }

    public ListResponse list(String authToken) throws DataAccessException{
        if (authAccess.getAuth(authToken) == null){
            throw new UnauthorizedException(401, "Error: unauthorized");
        }
        return new ListResponse(gameAccess.listGames());
    }

    public JoinResponse join(JoinRequest request, String authToken)
            throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException{
        if (authAccess.getAuth(authToken) == null){
            throw new UnauthorizedException(401, "Error: unauthorized");
        }
        if (request.gameID() == null || gameAccess.getGame(request.gameID()) == null || request.playerColor() == null ||
                (request.playerColor() != ChessGame.TeamColor.WHITE &&
                        request.playerColor() != ChessGame.TeamColor.BLACK
                        )){
            throw new BadRequestException(400, "Error: bad request");
        }
        GameData gameData = gameAccess.getGame(request.gameID());
        AuthData authData = authAccess.getAuth(authToken);
        GameData newGameData = gameData;
        if (request.playerColor() == ChessGame.TeamColor.BLACK){
            if (gameData.blackUsername() != null){
                throw new AlreadyTakenException(403, "Error: already taken");
            } else {
                newGameData = new GameData(request.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
            }
        } else if (request.playerColor() == ChessGame.TeamColor.WHITE) {
            if (gameData.whiteUsername() != null){
                throw new AlreadyTakenException(403, "Error: already taken");
            } else {
                newGameData = new GameData(request.gameID(), authData.username(), gameData.blackUsername() , gameData.gameName(), gameData.game());
            }
        }
        gameAccess.updateGame(newGameData);
        return new JoinResponse();
    }
}

