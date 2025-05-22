package dataaccess;

public interface GameDAO {
    void deleteAllGames();

    int createGame(String whiteUsername, String blackUsername, String gameName, chess.ChessGame chessGame);

    int generateGameID();
}
