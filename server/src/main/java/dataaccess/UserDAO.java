package dataaccess;

import exception.AlreadyTakenException;
import requests.RegisterRequest;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws AlreadyTakenException;

    void createUser(RegisterRequest request);

    void deleteAllUsers();
}
