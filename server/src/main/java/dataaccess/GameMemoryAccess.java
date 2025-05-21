package dataaccess;

import model.GameData;

import java.util.HashMap;

public class GameMemoryAccess implements GameDAO{
    final private HashMap<String, GameData> allGameData = new HashMap<>();

    public void deleteAllGames() {
        allGameData.clear();
    }
}
