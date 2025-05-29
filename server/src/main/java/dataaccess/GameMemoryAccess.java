package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GameMemoryAccess implements GameDAO{
    final private HashMap<Integer, GameData> allGameData = new HashMap<>();
    private int gameID = 1;

    public void deleteAllGames() {
        allGameData.clear();
    }

    public int createGame(String whiteUsername, String blackUsername, String gameName, chess.ChessGame chessGame){
        Integer gameID = (Integer) generateGameID();
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        allGameData.put(gameID, game);
        return gameID;
    }

    private int generateGameID(){
        return gameID++;
    }

    public GameData getGame(int gameID){
        return allGameData.get(gameID);
    }

    public ArrayList<GameData> listGames(){
        ArrayList<GameData> gameList = new ArrayList<>();
        gameList.addAll(allGameData.values());
        return gameList;
    }

    public void updateGame(GameData gameData){
        allGameData.remove(gameData.gameID());
        allGameData.put(gameData.gameID(), gameData);
    }
}
