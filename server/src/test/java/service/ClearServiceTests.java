package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responses.CreateResponse;
import responses.RegisterResponse;
import static org.junit.jupiter.api.Assertions.*;


public class ClearServiceTests {
    static final UserDAO UserAccess = new UserMemoryAccess();
    static final AuthDAO AuthAccess = new AuthMemoryAccess();
    static final GameDAO GameAccess = new GameMemoryAccess();
    static final ClearService ClearService = new ClearService(UserAccess, AuthAccess, GameAccess);
    TestHelperMethods methods = new TestHelperMethods(UserAccess, AuthAccess, GameAccess);

    @BeforeEach
    void startup(){
        ClearService.clear();
    }

    @Test
    void successfulClear() throws AlreadyTakenException {
        RegisterResponse registerResponse1 = methods.registerUser("Michael","pass","email");
        RegisterResponse registerResponse2 = methods.registerUser("Michael2","pass","email");
        CreateResponse createResponse1 = methods.createGame(registerResponse1.authToken(),"game1");
        CreateResponse createResponse2 = methods.createGame(registerResponse2.authToken(),"game1");
        ClearService.clear();
        assertTrue(UserAccess.getUser("Michael") == null && UserAccess.getUser("Michael2") == null);
        assertTrue(AuthAccess.getAuth(registerResponse1.authToken()) == null && AuthAccess.getAuth(registerResponse2.authToken()) == null);
        assertTrue(GameAccess.listGames().isEmpty());
    }
}
