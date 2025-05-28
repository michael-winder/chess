package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LogoutRequest;
import responses.LoginResponse;
import responses.RegisterResponse;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final UserDAO USER_ACESS = new UserMemoryAccess();
    static final AuthDAO AUTH_ACCESS = new AuthMemoryAccess();
    static final GameDAO GAME_ACCESS = new GameMemoryAccess();
    static final UserService USER_SERVICE = new UserService(USER_ACESS, AUTH_ACCESS, GAME_ACCESS);
    static final ClearService CLEAR_SERVICE = new ClearService(USER_ACESS, AUTH_ACCESS, GAME_ACCESS);
    static final TestHelperMethods METHODS = new TestHelperMethods(USER_ACESS, AUTH_ACCESS, GAME_ACCESS);
    @BeforeEach
    void startup() throws DataAccessException{
        CLEAR_SERVICE.clear();
    }

    @Test
    void registerUser() throws AlreadyTakenException, BadRequestException, DataAccessException {
        String username = "Michael";
        RegisterResponse actual = METHODS.registerUser(username,"pass","mail");
        assertEquals(username,actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void userTakenException() throws AlreadyTakenException, BadRequestException, DataAccessException{
        String username = "user";
        RegisterResponse response1 = METHODS.registerUser(username,"pass","email");
        assertThrows(AlreadyTakenException.class,() -> METHODS.registerUser(username,"pass","email"));
    }

    @Test
    void badRequestException() throws AlreadyTakenException, BadRequestException, DataAccessException{
        assertThrows(BadRequestException.class,() -> METHODS.registerUser("user",null,"email"));
    }

    @Test
    void loginRequest() throws BadRequestException, AlreadyTakenException, DataAccessException {
        METHODS.registerUser("Michael","pass","email");
        LoginResponse response = METHODS.loginUser("Michael","pass");
        assertEquals("Michael",response.username());
        assertNotNull(response.authToken());
    }

    @Test
    void incorrectPassword() throws BadRequestException, AlreadyTakenException, DataAccessException{
        METHODS.registerUser("Michael","pass","email");
        assertThrows(UnauthorizedException.class, () -> METHODS.loginUser("Michael","wrong"));
    }

    @Test
    void noAccount() throws BadRequestException, DataAccessException{
        assertThrows(UnauthorizedException.class, () -> METHODS.loginUser("Michael","wrong"));
    }

    @Test
    void noUsername() throws BadRequestException, DataAccessException{
        assertThrows(BadRequestException.class, () -> METHODS.loginUser(null,"wrong"));
    }

    @Test
    void logoutSuccess() throws UnauthorizedException, AlreadyTakenException, DataAccessException{
        RegisterResponse registerResponse = METHODS.registerUser("Michael","pass","email");
        METHODS.loginUser("Michael","pass");
        String authToken = registerResponse.authToken();
        METHODS.logoutUser(authToken);
        assertNull(AUTH_ACCESS.getAuth(authToken));
    }

    @Test
    void tokenNotExist() throws UnauthorizedException, AlreadyTakenException, DataAccessException{
        RegisterResponse registerResponse = METHODS.registerUser("Michael","pass","email");
        METHODS.loginUser("Michael","pass");
        assertThrows(UnauthorizedException.class, () -> METHODS.logoutUser("wrongToken"));
    }

    @Test
    void nullRequest() throws UnauthorizedException, AlreadyTakenException, DataAccessException{
        RegisterResponse registerResponse = METHODS.registerUser("Michael","pass","email");
        METHODS.loginUser("Michael","pass");
        LogoutRequest request = null;
        assertThrows(UnauthorizedException.class, () -> USER_SERVICE.logout(request));
    }
}
