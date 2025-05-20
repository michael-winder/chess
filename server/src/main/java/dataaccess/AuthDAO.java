package dataaccess;

import exception.AlreadyTakenException;
import model.AuthData;
import model.RegisterRequest;
import model.UserData;

import java.util.UUID;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws AlreadyTakenException;

    void createAuth(String username);

    static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
