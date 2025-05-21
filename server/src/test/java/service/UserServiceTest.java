package service;

import exception.AlreadyTakenException;
import exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
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
        RegisterRequest request = new RegisterRequest(username,"pass","email");
        RegisterResponse actual = userService.register(request);
        assertEquals(username,actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void userTakenException() throws AlreadyTakenException, BadRequestException{
        String username = "user";
        RegisterRequest request1 = new RegisterRequest(username,"pass","email");
        RegisterRequest request2 = new RegisterRequest(username,"pass","email");
        RegisterResponse response1 = userService.register(request1);
        assertThrows(AlreadyTakenException.class,() -> userService.register(request2));
    }

    @Test
    void badRequestException() throws AlreadyTakenException, BadRequestException{
        RegisterRequest request1 = new RegisterRequest("user",null,"email");
        assertThrows(BadRequestException.class,() -> userService.register(request1));
    }


}
