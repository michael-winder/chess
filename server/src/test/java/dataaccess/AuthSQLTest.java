package dataaccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class AuthSQLTest {
    static AuthSQLAccess authSQLAccess;
    private static Connection conn;

    @BeforeAll
    public static void setupSQL() throws DataAccessException{
        authSQLAccess = new AuthSQLAccess();
    }

    @BeforeEach
    public void setup () throws DataAccessException, SQLException{
        conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);
    }

    @AfterEach
    public void reset() throws DataAccessException, SQLException {
        authSQLAccess.deleteAllAuth();
        conn.rollback();
    }

    @Test
    public void createAuthTest() throws DataAccessException{
        String authToken = authSQLAccess.createAuth("Michael");
        HashMap<String, String> auths = loadAuths();
        assertTrue(auths.containsKey(authToken));
    }

    @Test
    public void failedCreate() {
        assertThrows(Exception.class, () -> authSQLAccess.createAuth(null));
    }

    @Test
    public void getAuthTest() throws DataAccessException{
        String authToken = authSQLAccess.createAuth("Michael");
        AuthData auth = authSQLAccess.getAuth(authToken);
        assertTrue(auth.authToken().equals(authToken) && auth.username().equals("Michael"));
    }

    @Test
    public void failedGetAuth() throws DataAccessException{
        AuthData data = authSQLAccess.getAuth("wrong");
        assertNull(data);
    }

    @Test
    public void deleteAllAuthTest() throws DataAccessException{
        String authToken = authSQLAccess.createAuth("Michael");
        String authToken2 = authSQLAccess.createAuth("Jacob");
        authSQLAccess.deleteAllAuth();
        HashMap<String, String> auths = loadAuths();
        assertTrue(auths.isEmpty());
    }

    @Test
    public void deleteAuthTest() throws DataAccessException{
        String authToken = authSQLAccess.createAuth("Michael");
        String authToken2 = authSQLAccess.createAuth("Jacob");
        authSQLAccess.deleteAuth(authToken);
        HashMap<String, String> auths = loadAuths();
        assertFalse(auths.containsKey(authToken));
        assertTrue(auths.containsKey(authToken2));
    }


    private HashMap<String, String> loadAuths() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            HashMap<String, String> auths = new HashMap<>();
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM auth")){
                try (var rs = preparedStatement.executeQuery()){
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String authToken = rs.getString("authToken");
                        auths.put(authToken, username);
                    }
                    return auths;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to load auths", e);
        }
    }
}
