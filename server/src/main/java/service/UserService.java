package service;

import dataaccess.AuthDAO;
import dataaccess.AuthMemoryAccess;
import dataaccess.UserDAO;
import dataaccess.UserMemoryAccess;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.RegisterResponse;

import java.util.Objects;

public class UserService {
    private final UserDAO userAccess = new UserMemoryAccess();
    private final AuthDAO authAccess = new AuthMemoryAccess();

//    public UserService(UserDAO userAccess,AuthDAO authAccess){
//        this.userAccess = userAccess;
//    }


    public RegisterResponse register(RegisterRequest request) throws AlreadyTakenException, BadRequestException{
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

    public LoginResponse login(LoginRequest r) throws BadRequestException{
        if (r.password() == null || r.username() == null){
            throw new BadRequestException(400,"Error: unauthorized");
        }
        UserData userData = userAccess.getUser(r.username());
        if (userData == null){
            throw new BadRequestException(401,"Error: unauthorized");
        } else if (!Objects.equals(userData.password(), r.password())){
            throw new BadRequestException(400,"Error: bad request");
        }
        String authToken = authAccess.createAuth(r.username());
        return new LoginResponse(r.username(),authToken);
    }

}
