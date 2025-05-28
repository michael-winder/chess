package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responses.CreateResponse;
import responses.RegisterResponse;
import static org.junit.jupiter.api.Assertions.*;


public class ClearServiceTests {
    static final UserDAO USER_ACCESS = new UserMemoryAccess();
    static final AuthDAO AUTH_ACCESS = new AuthMemoryAccess();
    static final GameDAO GAME_ACCESS = new GameMemoryAccess();
    static final ClearService CLEAR_SERVICE = new ClearService(USER_ACCESS, AUTH_ACCESS, GAME_ACCESS);
    static final TestHelperMethods METHODS = new TestHelperMethods(USER_ACCESS, AUTH_ACCESS, GAME_ACCESS);

    @BeforeEach
    void startup() throws DataAccessException{
        CLEAR_SERVICE.clear();
    }

    @Test
    void successfulClear() throws AlreadyTakenException, DataAccessException {
        RegisterResponse registerResponse1 = METHODS.registerUser("Michael","pass","email");
        RegisterResponse registerResponse2 = METHODS.registerUser("Michael2","pass","email");
        CreateResponse createResponse1 = METHODS.createGame(registerResponse1.authToken(),"game1");
        CreateResponse createResponse2 = METHODS.createGame(registerResponse2.authToken(),"game1");
        CLEAR_SERVICE.clear();
        assertTrue(USER_ACCESS.getUser("Michael") == null && USER_ACCESS.getUser("Michael2") == null);
        assertTrue(AUTH_ACCESS.getAuth(registerResponse1.authToken()) == null && AUTH_ACCESS.getAuth(registerResponse2.authToken()) == null);
        assertTrue(GAME_ACCESS.listGames().isEmpty());
    }
}
