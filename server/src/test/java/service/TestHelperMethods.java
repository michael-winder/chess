package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import requests.CreateRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.CreateResponse;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;

public class TestHelperMethods {
    final UserDAO userAccess;
    final AuthDAO authAccess;
    final GameDAO gameAccess;
    final UserService userService;
    final ClearService clearService;
    final GameService gameService;
    public TestHelperMethods(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        this.userService = new UserService(userAccess, authAccess, gameAccess);
        this.clearService = new ClearService(userAccess, authAccess, gameAccess);
        this.gameService = new GameService(userAccess, authAccess, gameAccess);
    }


    public RegisterResponse registerUser(String username, String password, String email) throws AlreadyTakenException, DataAccessException {
        RegisterRequest request = new RegisterRequest(username,password,email);
        return userService.register(request);
    }

    public LoginResponse loginUser(String username, String password) throws DataAccessException{
        LoginRequest request = new LoginRequest(username,password);
        return userService.login(request);
    }

    public LogoutResponse logoutUser(String authToken) throws DataAccessException{
        LogoutRequest request = new LogoutRequest(authToken);
        return userService.logout(request);
    }

    public CreateResponse createGame(String authToken, String gameName) throws DataAccessException{
        CreateRequest request = new CreateRequest(gameName);
        return gameService.create(request, authToken);
    }
}

