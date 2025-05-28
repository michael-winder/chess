package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserSQLTest {
    static UserSQLAccess USER_SQL_ACCESS ;
    private static Connection conn;


    @BeforeAll
    public static void setupSQL() throws DataAccessException{
        USER_SQL_ACCESS = new UserSQLAccess();
    }

    @BeforeEach
    public void setup () throws DataAccessException, SQLException{
        conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);
    }

    @AfterEach
    public void reset() throws SQLException {
        USER_SQL_ACCESS.deleteAllUsers();
        conn.rollback();
    }

    @Test
    public void createUserTest() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("Michael","pass","email");
        USER_SQL_ACCESS.createUser(request);
        List<String> users = loadUsers();
        assertTrue(users.contains("Michael"));
    }

    @Test
    public void deleteAllTest() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("Michael","pass","email");
        USER_SQL_ACCESS.createUser(request);
        USER_SQL_ACCESS.deleteAllUsers();
        List<String> users = loadUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    public void getUserTest() {
        RegisterRequest request = new RegisterRequest("Michael","pass","email");
        USER_SQL_ACCESS.createUser(request);
        UserData user = USER_SQL_ACCESS.getUser("Michael");
        assertTrue(user.username().equals("Michael") &&
                user.password().equals("pass") &&
                user.email().equals("email"));
    }

    private List<String> loadUsers() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            List<String> users = new ArrayList<>();
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM user")){
                try (var rs = preparedStatement.executeQuery()){
                    while (rs.next()) {
                        String username = rs.getString("username");
                        users.add(username);
                    }
                    return users;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to load users", e);
        }
    }

}
