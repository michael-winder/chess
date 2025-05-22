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
    static final UserDAO UserAccess = new UserMemoryAccess();
    static final AuthDAO AuthAccess = new AuthMemoryAccess();
    static final GameDAO GameAccess = new GameMemoryAccess();
    static final UserService UserService = new UserService(UserAccess, AuthAccess, GameAccess);
    static final ClearService ClearService = new ClearService(UserAccess, AuthAccess, GameAccess);
    static final TestHelperMethods Methods = new TestHelperMethods(UserAccess, AuthAccess, GameAccess);
    @BeforeEach
    void startup(){
        ClearService.clear();
    }

    @Test
    void registerUser() throws AlreadyTakenException, BadRequestException {
        String username = "Michael";
        RegisterResponse actual = Methods.registerUser(username,"pass","mail");
        assertEquals(username,actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void userTakenException() throws AlreadyTakenException, BadRequestException{
        String username = "user";
        RegisterResponse response1 = Methods.registerUser(username,"pass","email");
        assertThrows(AlreadyTakenException.class,() -> Methods.registerUser(username,"pass","email"));
    }

    @Test
    void badRequestException() throws AlreadyTakenException, BadRequestException{
        assertThrows(BadRequestException.class,() -> Methods.registerUser("user",null,"email"));
    }

    @Test
    void loginRequest() throws BadRequestException, AlreadyTakenException{
        Methods.registerUser("Michael","pass","email");
        LoginResponse response = Methods.loginUser("Michael","pass");
        assertEquals("Michael",response.username());
        assertNotNull(response.authToken());
    }

    @Test
    void incorrectPassword() throws BadRequestException, AlreadyTakenException{
        Methods.registerUser("Michael","pass","email");
        assertThrows(UnauthorizedException.class, () -> Methods.loginUser("Michael","wrong"));
    }

    @Test
    void noAccount() throws BadRequestException{
        assertThrows(UnauthorizedException.class, () -> Methods.loginUser("Michael","wrong"));
    }

    @Test
    void noUsername() throws BadRequestException{
        assertThrows(BadRequestException.class, () -> Methods.loginUser(null,"wrong"));
    }

    @Test
    void logoutSuccess() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = Methods.registerUser("Michael","pass","email");
        Methods.loginUser("Michael","pass");
        String authToken = registerResponse.authToken();
        Methods.logoutUser(authToken);
        assertNull(AuthAccess.getAuth(authToken));
    }

    @Test
    void tokenNotExist() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = Methods.registerUser("Michael","pass","email");
        Methods.loginUser("Michael","pass");
        assertThrows(UnauthorizedException.class, () -> Methods.logoutUser("wrongToken"));
    }

    @Test
    void nullRequest() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = Methods.registerUser("Michael","pass","email");
        Methods.loginUser("Michael","pass");
        LogoutRequest request = null;
        assertThrows(UnauthorizedException.class, () -> UserService.logout(request));
    }
}
