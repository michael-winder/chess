package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class GameSQLTest {
    static GameSQLAccess GAME_SQL ;
    private static Connection conn;

    @BeforeAll
    public static void setupSQL() throws DataAccessException{
        GAME_SQL = new GameSQLAccess();
    }

    @BeforeEach
    public void setup () throws DataAccessException, SQLException{
        conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false);
    }

    @AfterEach
    public void reset() throws DataAccessException, SQLException {
        GAME_SQL.deleteAllGames();
        conn.rollback();
    }

    @Test
    public void createGameTest() throws DataAccessException{
        int id = addGame("Michael", "Mike", "game1");
        HashMap<Integer, GameData> games = loadGames();
        GameData data = games.get(id);
        assertEquals("Michael", data.whiteUsername());
    }

    @Test
    public void  getGameTest() throws DataAccessException{
        int id = addGame("Jacob", "Jake", "game1");
        GameData data = GAME_SQL.getGame(id);
        assertEquals("Jacob", data.whiteUsername());
    }

    @Test
    public void listGames() throws DataAccessException{
        int id = addGame("Andrew", "Andy", "game1");
        int id2 = addGame("Michael", "Mike", "game2");
        HashMap<Integer, GameData> gameMap = loadGames();
        ArrayList<GameData> gameList = GAME_SQL.listGames();
        assertEquals(gameMap.get(id), gameList.get(0));
        assertEquals(gameMap.get(id2), gameList.get(1));
    }

    @Test
    public void deleteAllTest() throws DataAccessException{
        int id = addGame("Andrew", "Andy", "game1");
        int id2 = addGame("Michael", "Mike", "game2");
        GAME_SQL.deleteAllGames();
        HashMap<Integer, GameData> gameMap = loadGames();
        assertTrue(gameMap.isEmpty());
    }

    @Test
    public void updateGameTest() throws DataAccessException{
        int id = addGame("Andrew", "Andy", "game1");
        ChessGame game2 = new ChessGame();
        GameData gameData = new GameData(id, "James", "Jim", "game", game2);
        GAME_SQL.updateGame(gameData);
        HashMap<Integer, GameData> gameMap = loadGames();
        assertEquals("James", gameMap.get(id).whiteUsername());
    }

    private HashMap<Integer, GameData> loadGames() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            HashMap<Integer, GameData> games = new HashMap<>();
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game")){
                try (var rs = preparedStatement.executeQuery()){
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String gameJson = rs.getString("chessGame");
                        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                        GameData data = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                        games.put(gameID, data);
                    }
                    return games;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to load auths", e);
        }
    }

    private int addGame(String whiteUsername, String blackUsername, String gameName) throws DataAccessException{
        ChessGame game = new ChessGame();
        return GAME_SQL.createGame(whiteUsername, blackUsername, blackUsername, game);
    }
}
