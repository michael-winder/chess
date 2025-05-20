package dataaccess;

import exception.AlreadyTakenException;
import model.AuthData;
import model.RegisterRequest;
import model.UserData;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws AlreadyTakenException;

    void createAuth();
}
