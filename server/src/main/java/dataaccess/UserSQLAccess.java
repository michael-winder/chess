package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import requests.RegisterRequest;
import model.UserData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class UserSQLAccess implements UserDAO{

    public UserSQLAccess() throws ResponseException{
        configureDatabase();
    }

    public void createUser(RegisterRequest request) throws ResponseException{
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?,?, ?)")){
                preparedStatement.setString(1, request.username());
                preparedStatement.setString(2, request.password());
                preparedStatement.setString(3, request.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", "createUSer", e.getMessage()));
        }
    }


    public UserData getUser(String username) {
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
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to get user: %s", e.getMessage()));
        }
    }

    public void deleteAllUsers() throws ResponseException{
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("TRUNCATE TABLE user");
            ps.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to get delete users: %s", e.getMessage()));
        }
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


    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (var statement : createStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException| DataAccessException ex){
            throw new ResponseException(500, "Unable to configure database");
        }

    }
}
