package responses;

import model.GameData;

import java.util.ArrayList;

public record ClearResponse(ArrayList<GameData> list) {
}
