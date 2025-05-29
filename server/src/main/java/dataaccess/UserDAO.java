package dataaccess;

import exception.AlreadyTakenException;
import requests.RegisterRequest;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;

    void createUser(RegisterRequest request) throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;

    boolean verifyUser(String username, String password) throws DataAccessException;
}
