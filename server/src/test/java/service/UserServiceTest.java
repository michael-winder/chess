package service;

import exception.AlreadyTakenException;
import exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.RegisterResponse;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;

public class UserServiceTest {
    static final UserService userService = new UserService();
    static final ClearService clearService = new ClearService();

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
        assertThrows(BadRequestException.class,()->loginUser("Michael","wrong"));
    }

    @Test
    void noAccount() throws BadRequestException{
        assertThrows(BadRequestException.class,()->loginUser("Michael","wrong"));
    }

    @Test
    void noUsername() throws BadRequestException{
        assertThrows(BadRequestException.class,()->loginUser(null,"wrong"));
    }

    private RegisterResponse registerUser (String username, String password, String email) throws AlreadyTakenException{
        RegisterRequest request = new RegisterRequest(username,password,email);
        return userService.register(request);
    }

    private LoginResponse loginUser(String username, String password){
        LoginRequest request = new LoginRequest(username,password);
        return userService.login(request);
    }
}
