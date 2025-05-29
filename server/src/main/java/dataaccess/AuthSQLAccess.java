package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class AuthSQLAccess implements AuthDAO{
    public GameSQLAccess gameSQLAccess = new GameSQLAccess();
    public AuthSQLAccess() throws DataAccessException{
        gameSQLAccess.configureDatabase(createStatements);
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM auth WHERE authToken=?")){
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()){
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(authToken, username);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get auth", e);
        }
    }

    public String createAuth(String username) throws DataAccessException{
        String authToken = AuthDAO.generateToken();
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?,?)")){
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create authToken", e);
        }
        return authToken;
    }

    public void deleteAllAuth() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("TRUNCATE TABLE auth");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear all auth", e);
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("DELETE FROM auth WHERE authToken=?");
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear authToken", e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
            authToken varchar(255) NOT NULL,
            username varchar(255) NOT NULL,
            PRIMARY KEY (authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
