package service;

import dataaccess.UserDAO;
import exception.AlreadyTakenException;
import model.RegisterRequest;
import model.RegisterResponse;

public class UserService {
    private final UserDAO userAccess;

    public UserService(UserDAO userAccess){
        this.userAccess = userAccess;
    }


    public RegisterResponse register(RegisterRequest request) throws AlreadyTakenException {
        return userAccess.createUser(request);
    }

}
