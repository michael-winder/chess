package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GameSQLAccess implements GameDAO{

    public GameSQLAccess() throws DataAccessException{
        configureDatabase();
    }

    public void deleteAllGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("TRUNCATE TABLE game");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear all games", e);
        }
    }

    public int createGame(String whiteUsername, String blackUsername, String gameName, chess.ChessGame chessGame) throws DataAccessException{
        var chess = new Gson().toJson(chessGame);
        int gameID = 0;
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setString(1, whiteUsername);
                preparedStatement.setString(2, blackUsername);
                preparedStatement.setString(3,gameName);
                preparedStatement.setString(4, chess);
                preparedStatement.executeUpdate();
                var rs = preparedStatement.getGeneratedKeys();
                if (rs.next()){
                    gameID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game", e);
        }
        return gameID;
    }

    public int generateGameID(){
        return 1;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?")){
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()){
                    if (rs.next()) {
                        return readGame(rs);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game", e);
        }
    }

    public ArrayList<GameData> listGames() throws DataAccessException{
        ArrayList<GameData> gameList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gameList.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e){
            throw new DataAccessException("Unable to get game", e);
        }
        return gameList;
    }

    public void updateGame(GameData gameData) throws DataAccessException{
        var chess = new Gson().toJson(gameData.game());
        deleteGame(gameData.gameID());
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)")){
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.whiteUsername());
                preparedStatement.setString(3, gameData.blackUsername());
                preparedStatement.setString(4,gameData.gameName());
                preparedStatement.setString(5, chess);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update game", e);
        }
    }

    private void deleteGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement("DELETE FROM game WHERE gameID=?");
            ps.setInt(1, gameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete game", e);
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException{
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        var json = rs.getString("chessGame");
        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
            gameID int NOT NULL AUTO_INCREMENT,
            whiteUsername varchar(255),
            blackUsername varchar(255),
            gameName varchar(255) NOT NULL,
            chessGame TEXT DEFAULT NULL,
            PRIMARY KEY (gameID)
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
