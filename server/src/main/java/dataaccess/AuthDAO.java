package dataaccess;

import exception.AlreadyTakenException;
import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws  DataAccessException;

    String createAuth(String username) throws DataAccessException;

    static String generateToken(){
        return UUID.randomUUID().toString();
    }

    void deleteAllAuth() throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
