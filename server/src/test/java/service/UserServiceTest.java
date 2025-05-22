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
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final UserDAO userAccess = new UserMemoryAccess();
    static final AuthDAO authAccess = new AuthMemoryAccess();
    static final GameDAO gameAccess = new GameMemoryAccess();
    static final UserService userService = new UserService(userAccess, authAccess, gameAccess);
    static final ClearService clearService = new ClearService(userAccess, authAccess, gameAccess);
    static final TestHelperMethods methods = new TestHelperMethods(userAccess, authAccess, gameAccess);
    @BeforeEach
    void startup(){
        clearService.clear();
    }

    @Test
    void registerUser() throws AlreadyTakenException, BadRequestException {
        String username = "Michael";
        RegisterResponse actual = methods.registerUser(username,"pass","mail");
        assertEquals(username,actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void userTakenException() throws AlreadyTakenException, BadRequestException{
        String username = "user";
        RegisterResponse response1 = methods.registerUser(username,"pass","email");
        assertThrows(AlreadyTakenException.class,() -> methods.registerUser(username,"pass","email"));
    }

    @Test
    void badRequestException() throws AlreadyTakenException, BadRequestException{
        assertThrows(BadRequestException.class,() -> methods.registerUser("user",null,"email"));
    }

    @Test
    void loginRequest() throws BadRequestException, AlreadyTakenException{
        methods.registerUser("Michael","pass","email");
        LoginResponse response = methods.loginUser("Michael","pass");
        assertEquals("Michael",response.username());
        assertNotNull(response.authToken());
    }

    @Test
    void incorrectPassword() throws BadRequestException, AlreadyTakenException{
        methods.registerUser("Michael","pass","email");
        assertThrows(UnauthorizedException.class, () -> methods.loginUser("Michael","wrong"));
    }

    @Test
    void noAccount() throws BadRequestException{
        assertThrows(UnauthorizedException.class, () -> methods.loginUser("Michael","wrong"));
    }

    @Test
    void noUsername() throws BadRequestException{
        assertThrows(BadRequestException.class, () -> methods.loginUser(null,"wrong"));
    }

    @Test
    void logoutSuccess() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        methods.loginUser("Michael","pass");
        String authToken = registerResponse.authToken();
        methods.logoutUser(authToken);
        assertNull(authAccess.getAuth(authToken));
    }

    @Test
    void tokenNotExist() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        methods.loginUser("Michael","pass");
        assertThrows(UnauthorizedException.class, () -> methods.logoutUser("wrongToken"));
    }

    @Test
    void nullRequest() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = methods.registerUser("Michael","pass","email");
        methods.loginUser("Michael","pass");
        LogoutRequest request = null;
        assertThrows(UnauthorizedException.class, () -> userService.logout(request));
    }
}
