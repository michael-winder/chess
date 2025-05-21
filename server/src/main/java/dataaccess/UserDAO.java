package dataaccess;

import exception.AlreadyTakenException;
import requests.RegisterRequest;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username);

    void createUser(RegisterRequest request);

    void deleteAllUsers();
}
