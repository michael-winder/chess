package model;

record UserData (String username, String password, String email){}

record GameData (int gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game){}

record AuthData (String authToken, String username){}