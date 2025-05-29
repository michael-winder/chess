package dataaccess;

import model.GameData;
import java.util.ArrayList;

public interface GameDAO {
    void deleteAllGames() throws DataAccessException;

    int createGame(String whiteUsername, String blackUsername, String gameName, chess.ChessGame chessGame) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;
}
