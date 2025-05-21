package dataaccess;

import model.GameData;

import java.util.HashMap;

public class GameMemoryAccess implements GameDAO{
    final private HashMap<Integer, GameData> allGameData = new HashMap<>();
    private int gameID = 1;

    public void deleteAllGames() {
        allGameData.clear();
    }

    public void createGame(String whiteUsername, String blackUsername, String gameName, chess.ChessGame chessGame){
        Integer gameID = (Integer) generateGameID();
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        allGameData.put(gameID, game);
    }

    public int generateGameID(){
        return gameID++;
    }
}
