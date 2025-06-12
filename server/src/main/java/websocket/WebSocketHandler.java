package websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMesage;
import websocket.messages.NotificationMessage;
import websocket.messages.LoadGameMessage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {
    public final UserDAO userAccess;
    public final AuthDAO authAccess;
    public final GameDAO gameAccess;
    Session session;
    private final ConnectionManager connections = new ConnectionManager();
    ChessGame chessGame;
    public WebSocketHandler() {
        try {
            this.userAccess = new UserSQLAccess();
            this.authAccess = new AuthSQLAccess();
            this.gameAccess = new GameSQLAccess();
        } catch (DataAccessException e){
            throw new ResponseException(500, "unable to initialize Database");
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> join(session, command);
            case LEAVE -> leave(session, command);
            case MAKE_MOVE -> makeMove(session, message);
            case RESIGN -> resign(session, command);
        }
    }

    private void join(Session session, UserGameCommand command) throws IOException, DataAccessException {
        this.session = session;
        if (authAccess.getAuth(command.getAuthToken()) == null || gameAccess.getGame(command.getGameID()) == null){
            sendError(session, "Error: invalid credentials\n");
        }
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        GameData gameData = gameAccess.getGame(command.getGameID());
        connections.add(gameData.gameID() ,authData.username(), session);
        if (Objects.equals(gameData.whiteUsername(), authData.username())){
            sendNotification(authData.username() + " has joined the game as white", authData.username(), true, command.getGameID());
        } else if (Objects.equals(gameData.blackUsername(), authData.username())){
            sendNotification(authData.username() + " has joined the game as black", authData.username(), true, command.getGameID());
        } else {
            sendNotification(authData.username() + " is observing the game", authData.username(), true, command.getGameID());
        }
        loadGameMessage(session, gameData, false);
    }

    private void leave(Session session,  UserGameCommand command) throws IOException, DataAccessException {
        this.session = session;
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        GameData gameData = gameAccess.getGame(command.getGameID());
        String blackUser = gameData.blackUsername();
        String whiteUser = gameData.whiteUsername();
        if (Objects.equals(gameData.whiteUsername(), authData.username())){
            whiteUser = null;
        } else if (Objects.equals(gameData.blackUsername(), authData.username())){
            blackUser = null;
        }
        GameData updatedGame = new GameData(command.getGameID(), whiteUser, blackUser, gameData.gameName(), gameData.game());
        gameAccess.updateGame(updatedGame);
        connections.remove(authData.username());
        sendNotification(authData.username() + " has left the game", authData.username(), true, command.getGameID());
    }

    private void makeMove(Session session,  String message) throws IOException, DataAccessException {
        this.session = session;
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        if (authAccess.getAuth(command.getAuthToken()) == null || gameAccess.getGame(command.getGameID()) == null){
            sendError(session, "Error: invalid credentials\n");
        }
        GameData gameData = gameAccess.getGame(command.getGameID());
        if (gameData.game().gameOver){
            sendError(session, "Error: This game is over\n");
            return;
        }
        chessGame = gameData.game();
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        chessGame.currentBoard = gameAccess.getGame(command.getGameID()).game().currentBoard;
        if (!checkTurn(getColor(gameData, authData), gameData.game())){
            sendError(session, "Error: it is not your turn!\n");
            return;
        }
        try {
            chessGame.makeMove(command.move);
        } catch (InvalidMoveException e){
            sendError(session, "Error: invalid move\n");
            return;
        }
        GameData updatedGame = new GameData(command.getGameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);
        gameAccess.updateGame(updatedGame);
        sendNotification(authData.username() + " has made a move from " + moveReturn(command.move.getStartPosition()) +
                " to " + moveReturn(command.move.getEndPosition()), authData.username(), true, command.getGameID());
        if (!teamInCheckMateMessenger(updatedGame)){
            isTeamInCheck(updatedGame);
        }
        if (isTeamInStaleMate(gameData)){
            sendNotification("Stalemate. GAME OVER!\n", "none121", true, gameData.gameID());
        }
        loadGameMessage(session, updatedGame, true);
    }

    private void resign(Session session,  UserGameCommand command) throws IOException, DataAccessException {
        if (authAccess.getAuth(command.getAuthToken()) == null || gameAccess.getGame(command.getGameID()) == null){
            sendError(session, "Error: invalid credentials\n");
        }
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        GameData gameData = gameAccess.getGame(command.getGameID());
        if (gameData.game().gameOver){
            sendError(session, "Game is already over\n");
            return;
        }
        if (!Objects.equals(gameData.blackUsername(), authData.username()) && !Objects.equals(gameData.whiteUsername(), authData.username())){
            sendError(session, "Error: Observers can't resign\n");
            return;
        }
        gameData.game().gameOver = true;
        gameAccess.updateGame(gameData);
        sendNotification(authData.username() + " has resigned. GAME OVER!\n", "none121", true, command.getGameID());
    }



    private ChessGame.TeamColor getColor(GameData gameData, AuthData authData){
        if (Objects.equals(authData.username(), gameData.blackUsername())){
            return  ChessGame.TeamColor.BLACK;
        } else if (Objects.equals(authData.username(), gameData.whiteUsername())){
            return ChessGame.TeamColor.WHITE;
        } else {
            return null;
        }
    }

    private boolean checkTurn(ChessGame.TeamColor color, ChessGame game){
        return game.getTeamTurn() == color;
    }

    private boolean isTeamInCheck(GameData gameData) throws IOException{
        if (gameData.game().isInCheck(ChessGame.TeamColor.WHITE)){
            sendNotification(gameData.whiteUsername()+ " is in check!", "none121", true, gameData.gameID());
            return true;
        } else if (gameData.game().isInCheck(ChessGame.TeamColor.BLACK)){
            sendNotification(gameData.blackUsername()+ " is in check!", "none121", true, gameData.gameID());
            return true;
        } else {
            return false;
        }
    }

    private boolean isTeamInStaleMate(GameData gameData){
        return gameData.game().isInStalemate(ChessGame.TeamColor.WHITE) || gameData.game().isInStalemate(ChessGame.TeamColor.BLACK);
    }

    private boolean teamInCheckMateMessenger(GameData gameData) throws IOException, DataAccessException {
        if (gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)){
            sendNotification(gameData.whiteUsername()+ " is in checkmate! GAME OVER\n", "none121", true, gameData.gameID());
            gameData.game().gameOver = true;
            gameAccess.updateGame(gameData);
            return true;
        } else if (gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)){
            sendNotification(gameData.blackUsername()+ " is in checkmate! GAME OVER\n", "none121", true, gameData.gameID());
            gameData.game().gameOver = true;
            gameAccess.updateGame(gameData);
            return true;
        } else {
            return false;
        }
    }

    private void sendNotification(String message, String username, boolean broadcast, int gameID) throws IOException{
        NotificationMessage notification = new NotificationMessage(message);
        String serverMessage = new Gson().toJson(notification);
        if (broadcast) {
            connections.broadcast(username, serverMessage, gameID);
        } else {
            session.getRemote().sendString(serverMessage);
        }
    }

    private void sendError(Session session, String errorMessage) throws IOException{
        ErrorMesage errorMesage = new ErrorMesage(errorMessage);
        String error = new Gson().toJson(errorMesage);
        session.getRemote().sendString(error);
    }

    private void loadGameMessage (Session session, GameData chessGame, boolean broadcast) throws IOException{
        LoadGameMessage gameMessage = new LoadGameMessage(chessGame);
        String game = new Gson().toJson(gameMessage);
        if (broadcast){
            connections.broadcast("none121", game, chessGame.gameID());
        } else {
            session.getRemote().sendString(game);
        }
    }

    private String moveReturn(ChessPosition chessPosition){
        int colChar = chessPosition.getColumn();
        int row = chessPosition.getRow();
        String position;
        if (colChar == 8){
            position = String.format("h%d", row);
        } else if (colChar == 7){
            position = String.format("g%d", row);
        } else if (colChar == 6){
            position = String.format("f%d", row);
        } else if (colChar == 5){
            position = String.format("e%d", row);
        } else if (colChar == 4){
            position = String.format("d%d", row);
        } else if (colChar == 3){
            position = String.format("c%d", row);
        } else if (colChar == 2){
            position = String.format("b%d", row);
        } else {
            position = String.format("a%d", row);
        }
        return position;
    }
}