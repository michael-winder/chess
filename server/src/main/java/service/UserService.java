package service;

import dataaccess.AuthDAO;
import dataaccess.AuthMemoryAccess;
import dataaccess.UserDAO;
import dataaccess.UserMemoryAccess;
import exception.AlreadyTakenException;
import model.RegisterRequest;
import model.RegisterResponse;

public class UserService {
    private final UserDAO userAccess = new UserMemoryAccess();
    private final AuthDAO authAccess = new AuthMemoryAccess();

//    public UserService(UserDAO userAccess,AuthDAO authAccess){
//        this.userAccess = userAccess;
//    }


    public RegisterResponse register(RegisterRequest request) throws AlreadyTakenException {
        if (userAccess.getUser(request.username()) != null){
            throw new AlreadyTakenException(403,"Username already taken");
        } else {
        userAccess.createUser(request);
        String authToken = authAccess.createAuth(request.username());
        return new RegisterResponse(request.username(),authToken);
        }
    }

}
