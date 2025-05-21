package dataaccess;

import exception.AlreadyTakenException;
import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    AuthData getAuth(String authToken);

    String createAuth(String username);

    static String generateToken(){
        return UUID.randomUUID().toString();
    }

    void deleteAllAuth();

    void deleteAuth(String authToken);
}
