package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responses.CreateResponse;
import responses.ListResponse;
import responses.RegisterResponse;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    static final UserDAO userAccess = new UserMemoryAccess();
    static final AuthDAO authAccess = new AuthMemoryAccess();
    static final GameDAO gameAccess = new GameMemoryAccess();
    static final UserService userService = new UserService(userAccess, authAccess, gameAccess);
    static final ClearService clearService = new ClearService(userAccess, authAccess, gameAccess);
    static final GameService gameService = new GameService(userAccess, authAccess, gameAccess);
    TestHelperMethods methods = new TestHelperMethods(userAccess, authAccess, gameAccess);

    @BeforeEach
    void startup(){
        clearService.clear();
    }

    @Test
    void createGame() throws AlreadyTakenException {
        RegisterResponse regResponse = methods.registerUser("Michael","pass","email");
        CreateResponse createResponse = methods.createGame(regResponse.authToken(), "MyGame");
        assertNotNull(gameAccess.getGame(createResponse.gameID()));
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
        GameData game1 = gameAccess.getGame(createResponse1.gameID());
        GameData game2 = gameAccess.getGame(createResponse2.gameID());
        ListResponse listResponse = gameService.list(registerResponse.authToken());
        assertTrue(listResponse.gameList().contains(game1) && listResponse.gameList().contains(game2));
    }

    @Test
    void listException() throws UnauthorizedException, AlreadyTakenException {
        RegisterResponse registerResponse = methods.registerUser("Michael", "pass", "email");
        CreateResponse createResponse1 = methods.createGame(registerResponse.authToken(), "game1");
        CreateResponse createResponse2 = methods.createGame(registerResponse.authToken(), "game2");
        assertThrows(UnauthorizedException.class, () -> gameService.list(null));
    }
}
