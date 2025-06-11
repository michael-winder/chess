package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
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

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {
    public final UserDAO userAccess;
    public final AuthDAO authAccess;
    public final GameDAO gameAccess;
    private final ConnectionManager connections = new ConnectionManager();
    ChessGame chessGame = new ChessGame();
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
        }
    }

    private void join(Session session, UserGameCommand command) throws IOException, DataAccessException {
        if (authAccess.getAuth(command.getAuthToken()) == null || gameAccess.getGame(command.getGameID()) == null){
            sendError(session, "Error: invalid credentials");
        }
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        GameData gameData = gameAccess.getGame(command.getGameID());
        connections.add(authData.username(), session);
        if (Objects.equals(gameData.whiteUsername(), authData.username())){
            sendNotification(authData.username() + " has joined the game as white", authData.username());
        } else if (Objects.equals(gameData.blackUsername(), authData.username())){
            sendNotification(authData.username() + " has joined the game as black", authData.username());
        } else {
            sendNotification(authData.username() + " is observing the game", authData.username());
        }
        loadGameMessage(session, gameData, false);
    }

    private void leave(Session session,  UserGameCommand command) throws IOException, DataAccessException {
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
        sendNotification(authData.username() + " has left the game", authData.username());
    }

    private void makeMove(Session session,  String message) throws IOException, DataAccessException {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        GameData gameData = gameAccess.getGame(command.getGameID());
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        chessGame.currentBoard = gameAccess.getGame(command.getGameID()).game().currentBoard;
        try {
            chessGame.makeMove(command.move);
        } catch (InvalidMoveException e){
            sendError(session, "Error: invalid move");
            return;
        }
        GameData updatedGame = new GameData(command.getGameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);
        gameAccess.updateGame(updatedGame);
        loadGameMessage(session, updatedGame, true);
        sendNotification(authData.username() + " has made a move", authData.username());
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

    private void sendNotification(String message, String username) throws IOException{
        NotificationMessage notification = new NotificationMessage(message);
        String serverMessage = new Gson().toJson(notification);
        connections.broadcast(username, serverMessage);
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
            connections.broadcast("none121", game);
        } else {
            session.getRemote().sendString(game);
        }
    }
}