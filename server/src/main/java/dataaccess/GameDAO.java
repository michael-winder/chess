package dataaccess;

import model.GameData;
import java.util.ArrayList;

public interface GameDAO {
    void deleteAllGames();

    int createGame(String whiteUsername, String blackUsername, String gameName, chess.ChessGame chessGame);

    int generateGameID();

    GameData getGame(int gameID);

    ArrayList<GameData> listGames();

    void updateGame(GameData gameData);
}
