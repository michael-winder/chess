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
    static final UserDAO USER_ACCESS = new UserMemoryAccess();
    static final AuthDAO AUTH_ACCESS = new AuthMemoryAccess();
    static final GameDAO GAME_ACCESS = new GameMemoryAccess();
    static final ClearService CLEAR_SERVICE = new ClearService(USER_ACCESS, AUTH_ACCESS, GAME_ACCESS);
    static final GameService GAME_SERVICE = new GameService(USER_ACCESS, AUTH_ACCESS, GAME_ACCESS);
    TestHelperMethods methods = new TestHelperMethods(USER_ACCESS, AUTH_ACCESS, GAME_ACCESS);

    @BeforeEach
    void startup() throws DataAccessException{
        CLEAR_SERVICE.clear();
    }

    @Test
    void createGame() throws AlreadyTakenException, DataAccessException {
        RegisterResponse regResponse = methods.registerUser("Michael","pass","email");
        CreateResponse createResponse = methods.createGame(regResponse.authToken(), "MyGame");
        assertNotNull(GAME_ACCESS.getGame(createResponse.gameID()));
    }

    @Test
    void createWithBadKey() throws BadRequestException, AlreadyTakenException, DataAccessException {
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        assertThrows(UnauthorizedException.class, () -> methods.createGame("badAuth","newGame"));
    }

    @Test
    void createWithNullName() throws BadRequestException, AlreadyTakenException, DataAccessException {
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        assertThrows(BadRequestException.class, () -> methods.createGame(registerResponse.authToken(),null));
    }

    @Test
    void listSuccess() throws UnauthorizedException, AlreadyTakenException, DataAccessException {
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        CreateResponse createResponse1 = methods.createGame(registerResponse.authToken(),"game1");
        CreateResponse createResponse2 = methods.createGame(registerResponse.authToken(),"game2");
        GameData game1 = GAME_ACCESS.getGame(createResponse1.gameID());
        GameData game2 = GAME_ACCESS.getGame(createResponse2.gameID());
        ListResponse listResponse = GAME_SERVICE.list(registerResponse.authToken());
        assertTrue(listResponse.games().contains(game1) && listResponse.games().contains(game2));
    }

    @Test
    void listException() throws UnauthorizedException, AlreadyTakenException, DataAccessException {
        RegisterResponse registerResponse = methods.registerUser("Michael", "pass", "email");
        CreateResponse createResponse1 = methods.createGame(registerResponse.authToken(), "game1");
        CreateResponse createResponse2 = methods.createGame(registerResponse.authToken(), "game2");
        assertThrows(UnauthorizedException.class, () -> GAME_SERVICE.list(null));
    }

    @Test
    void joinSuccess() throws AlreadyTakenException, DataAccessException{
        RegisterResponse registerResponse = methods.registerUser("Michael", "pass", "email");
        CreateResponse createResponse = methods.createGame(registerResponse.authToken(), "game1");
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.BLACK, createResponse.gameID());
        GAME_SERVICE.join(joinRequest, registerResponse.authToken());
        assertEquals("Michael", GAME_ACCESS.getGame(createResponse.gameID()).blackUsername());
    }

    @Test
    void joinColorTaken() throws AlreadyTakenException, DataAccessException{
        RegisterResponse registerResponse = methods.registerUser("Michael", "pass", "email");
        CreateResponse createResponse = methods.createGame(registerResponse.authToken(), "game1");
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.BLACK, createResponse.gameID());
        GAME_SERVICE.join(joinRequest, registerResponse.authToken());
        assertThrows(AlreadyTakenException.class, () -> GAME_SERVICE.join(joinRequest, registerResponse.authToken()));
    }
}
