package dataaccess;

import model.GameData;

public interface GameDAO {
    void deleteAllGames();

    int createGame(String whiteUsername, String blackUsername, String gameName, chess.ChessGame chessGame);

    int generateGameID();

    GameData getGame(int gameID);
}
