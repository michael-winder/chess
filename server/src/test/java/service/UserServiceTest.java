package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;

public class UserServiceTest {
    static final UserDAO userAccess = new UserMemoryAccess();
    static final AuthDAO authAccess = new AuthMemoryAccess();
    static final GameDAO gameAccess = new GameMemoryAccess();
    static final UserService userService = new UserService(userAccess, authAccess, gameAccess);
    static final ClearService clearService = new ClearService(userAccess, authAccess, gameAccess);

    @BeforeEach
    void startup(){
        clearService.clear();
    }

    @Test
    void registerUser() throws AlreadyTakenException, BadRequestException {
        String username = "Michael";
        RegisterResponse actual = registerUser(username,"pass","mail");
        assertEquals(username,actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void userTakenException() throws AlreadyTakenException, BadRequestException{
        String username = "user";
        RegisterResponse response1 = registerUser(username,"pass","email");
        assertThrows(AlreadyTakenException.class,() -> registerUser(username,"pass","email"));
    }

    @Test
    void badRequestException() throws AlreadyTakenException, BadRequestException{
        assertThrows(BadRequestException.class,() -> registerUser("user",null,"email"));
    }

    @Test
    void loginRequest() throws BadRequestException, AlreadyTakenException{
        registerUser("Michael","pass","email");
        LoginResponse response = loginUser("Michael","pass");
        assertEquals("Michael",response.username());
        assertNotNull(response.authToken());
    }

    @Test
    void incorrectPassword() throws BadRequestException, AlreadyTakenException{
        registerUser("Michael","pass","email");
        assertThrows(UnauthorizedException.class,()->loginUser("Michael","wrong"));
    }

    @Test
    void noAccount() throws BadRequestException{
        assertThrows(UnauthorizedException.class,()->loginUser("Michael","wrong"));
    }

    @Test
    void noUsername() throws BadRequestException{
        assertThrows(BadRequestException.class,()->loginUser(null,"wrong"));
    }

    @Test
    void logoutSuccess() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = registerUser("Michael","pass","email");
        loginUser("Michael","pass");
        String authToken = registerResponse.authToken();
        logoutUser(authToken);
        assertNull(authAccess.getAuth(authToken));
    }

    @Test
    void tokenNotExist() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = registerUser("Michael","pass","email");
        loginUser("Michael","pass");
        assertThrows(UnauthorizedException.class, () -> logoutUser("wrongToken"));
    }

    @Test
    void nullRequest() throws UnauthorizedException, AlreadyTakenException{
        RegisterResponse registerResponse = registerUser("Michael","pass","email");
        loginUser("Michael","pass");
        LogoutRequest request = null;
        assertThrows(UnauthorizedException.class, () -> userService.logout(request));
    }

    private RegisterResponse registerUser(String username, String password, String email) throws AlreadyTakenException{
        RegisterRequest request = new RegisterRequest(username,password,email);
        return userService.register(request);
    }

    private LoginResponse loginUser(String username, String password){
        LoginRequest request = new LoginRequest(username,password);
        return userService.login(request);
    }

    private LogoutResponse logoutUser(String authToken){
        LogoutRequest request = new LogoutRequest(authToken);
        return userService.logout(request);
    }
}
