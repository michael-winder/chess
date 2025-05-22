package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responses.CreateResponse;
import responses.RegisterResponse;
import static org.junit.jupiter.api.Assertions.*;


public class ClearServiceTests {
    static final UserDAO userAccess = new UserMemoryAccess();
    static final AuthDAO authAccess = new AuthMemoryAccess();
    static final GameDAO gameAccess = new GameMemoryAccess();
    static final ClearService clearService = new ClearService(userAccess, authAccess, gameAccess);
    TestHelperMethods methods = new TestHelperMethods(userAccess, authAccess, gameAccess);

    @BeforeEach
    void startup(){
        clearService.clear();
    }

    @Test
    void successfulClear() throws AlreadyTakenException {
        RegisterResponse registerResponse1 = methods.registerUser("Michael","pass","email");
        RegisterResponse registerResponse2 = methods.registerUser("Michael2","pass","email");
        CreateResponse createResponse1 = methods.createGame(registerResponse1.authToken(),"game1");
        CreateResponse createResponse2 = methods.createGame(registerResponse2.authToken(),"game1");
        clearService.clear();
        assertTrue(userAccess.getUser("Michael") == null && userAccess.getUser("Michael2") == null);
        assertTrue(authAccess.getAuth(registerResponse1.authToken()) == null && authAccess.getAuth(registerResponse2.authToken()) == null);
        assertTrue(gameAccess.listGames().isEmpty());
    }
}
