package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;

import java.util.Objects;

public class UserService {
    private final UserDAO userAccess ;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }


    public RegisterResponse register(RegisterRequest request) throws AlreadyTakenException, BadRequestException, DataAccessException{
        if (request.password() == null){
            throw new BadRequestException(400, "Error: bad request");
        }
        if (userAccess.getUser(request.username()) != null){
            throw new AlreadyTakenException(403,"Error: already taken");
        } else {
        userAccess.createUser(request);
        String authToken = authAccess.createAuth(request.username());
        return new RegisterResponse(request.username(),authToken);
        }
    }

    public LoginResponse login(LoginRequest r) throws BadRequestException, DataAccessException{
        if (r.password() == null || r.username() == null){
            throw new BadRequestException(400,"Error: bad request");
        }
        UserData userData = userAccess.getUser(r.username());
        if (userData == null){
            throw new UnauthorizedException(401,"Error: unauthorized");
        } else if (!userAccess.verifyUser(r.username(),r.password())){
            throw new UnauthorizedException(401,"Error: unauthorized");
        }
        String authToken = authAccess.createAuth(r.username());
        return new LoginResponse(r.username(),authToken);
    }

    public LogoutResponse logout(LogoutRequest r) throws UnauthorizedException, DataAccessException{
        if (r == null){
            throw new UnauthorizedException(401,"Error: unauthorized");
        }
        AuthData authData = authAccess.getAuth(r.authToken());
        if (authData == null){
            throw new UnauthorizedException(401,"Error: unauthorized");
        } else {
            authAccess.deleteAuth(r.authToken());
        }
        return new LogoutResponse();
    }

}
