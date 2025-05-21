package dataaccess;

public interface GameDAO {
    void deleteAllGames();

    void createGame(String whiteUsername, String blackUsername, String gameName, chess.ChessGame chessGame);

    int generateGameID();
}
