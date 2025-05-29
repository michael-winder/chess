package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requests.RegisterRequest;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class UserSQLAccess implements UserDAO{

    public UserSQLAccess() throws DataAccessException{
        configureDatabase();
    }

    public void createUser(RegisterRequest request) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?,?, ?)")){
                String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
                preparedStatement.setString(1, request.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, request.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }


    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT password, email FROM user WHERE username=?")){
                preparedStatement.setString(1,username);
                try (var rs = preparedStatement.executeQuery()){
                    if (rs.next()) {
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, password, email);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    public void deleteAllUsers() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("TRUNCATE TABLE user");
            ps.executeUpdate();
        } catch (SQLException e) {
           throw new DataAccessException("Unable to create user", e);
        }
    }

    public boolean verifyUser(String username, String password) throws DataAccessException{
        UserData user = getUser(username);
        return BCrypt.checkpw(password, user.password());
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
            username varchar(255) NOT NULL,
            password varchar(255) NOT NULL,
            email varchar(255),
            PRIMARY KEY (username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (var statement : createStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex){
            throw new DataAccessException("unable to configure Database");
        }

    }
}
