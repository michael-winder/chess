package service;

import chess.ChessGame;
import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.JoinRequest;
import responses.CreateResponse;
import responses.ListResponse;
import responses.RegisterResponse;
import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    static final UserDAO UserAccess = new UserMemoryAccess();
    static final AuthDAO AuthAccess = new AuthMemoryAccess();
    static final GameDAO GameAccess = new GameMemoryAccess();
    static final ClearService ClearService = new ClearService(UserAccess, AuthAccess, GameAccess);
    static final GameService GameService = new GameService(UserAccess, AuthAccess, GameAccess);
    TestHelperMethods methods = new TestHelperMethods(UserAccess, AuthAccess, GameAccess);

    @BeforeEach
    void startup(){
        ClearService.clear();
    }

    @Test
    void createGame() throws AlreadyTakenException {
        RegisterResponse regResponse = methods.registerUser("Michael","pass","email");
        CreateResponse createResponse = methods.createGame(regResponse.authToken(), "MyGame");
        assertNotNull(GameAccess.getGame(createResponse.gameID()));
    }

    @Test
    void createWithBadKey() throws BadRequestException, AlreadyTakenException {
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        assertThrows(UnauthorizedException.class, () -> methods.createGame("badAuth","newGame"));
    }

    @Test
    void createWithNullName() throws BadRequestException, AlreadyTakenException {
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        assertThrows(BadRequestException.class, () -> methods.createGame(registerResponse.authToken(),null));
    }

    @Test
    void listSuccess() throws UnauthorizedException, AlreadyTakenException {
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        CreateResponse createResponse1 = methods.createGame(registerResponse.authToken(),"game1");
        CreateResponse createResponse2 = methods.createGame(registerResponse.authToken(),"game2");
        GameData game1 = GameAccess.getGame(createResponse1.gameID());
        GameData game2 = GameAccess.getGame(createResponse2.gameID());
        ListResponse listResponse = GameService.list(registerResponse.authToken());
        assertTrue(listResponse.games().contains(game1) && listResponse.games().contains(game2));
    }

    @Test
    void listException() throws UnauthorizedException, AlreadyTakenException {
        RegisterResponse registerResponse = methods.registerUser("Michael", "pass", "email");
        CreateResponse createResponse1 = methods.createGame(registerResponse.authToken(), "game1");
        CreateResponse createResponse2 = methods.createGame(registerResponse.authToken(), "game2");
        assertThrows(UnauthorizedException.class, () -> GameService.list(null));
    }

    @Test
    void joinSuccess() throws AlreadyTakenException{
        RegisterResponse registerResponse = methods.registerUser("Michael", "pass", "email");
        CreateResponse createResponse = methods.createGame(registerResponse.authToken(), "game1");
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.BLACK, createResponse.gameID());
        GameService.join(joinRequest, registerResponse.authToken());
        assertEquals("Michael", GameAccess.getGame(createResponse.gameID()).blackUsername());
    }

    @Test
    void joinColorTaken() throws AlreadyTakenException{
        RegisterResponse registerResponse = methods.registerUser("Michael", "pass", "email");
        CreateResponse createResponse = methods.createGame(registerResponse.authToken(), "game1");
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.BLACK, createResponse.gameID());
        GameService.join(joinRequest, registerResponse.authToken());
        assertThrows(AlreadyTakenException.class, () -> GameService.join(joinRequest, registerResponse.authToken()));
    }
}
