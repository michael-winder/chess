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
            ErrorMesage errorMesage = new ErrorMesage("Error: invalid credentials");
            String error = new Gson().toJson(errorMesage);
            session.getRemote().sendString(error);
        }
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        GameData gameData = gameAccess.getGame(command.getGameID());
        connections.add(authData.username(), session);
        NotificationMessage notification;
        if (Objects.equals(gameData.whiteUsername(), authData.username())){
            notification = new NotificationMessage(authData.username() + " has joined the game as white");
        } else if (Objects.equals(gameData.blackUsername(), authData.username())){
            notification = new NotificationMessage(authData.username() + " has joined the game as black");
        } else {
            notification = new NotificationMessage(authData.username() + " is observing the game");
        }
        String serverMessage = new Gson().toJson(notification);
        connections.broadcast(authData.username(), serverMessage);
        LoadGameMessage gameMessage = new LoadGameMessage(gameData);
        String game = new Gson().toJson(gameMessage);
        session.getRemote().sendString(game);
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
        NotificationMessage notification = new NotificationMessage(authData.username() + " has left the game");
        String serverMessage = new Gson().toJson(notification);
        connections.broadcast(authData.username(), serverMessage);
    }

    private void makeMove(Session session,  String message) throws IOException, DataAccessException {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        GameData gameData = gameAccess.getGame(command.getGameID());
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        chessGame.currentBoard = gameAccess.getGame(command.getGameID()).game().currentBoard;
        try {
            chessGame.makeMove(command.move);
        } catch (InvalidMoveException e){
            ErrorMesage errorMesage = new ErrorMesage("Error: invalid move");
            String error = new Gson().toJson(errorMesage);
            session.getRemote().sendString(error);
            return;
        }
        GameData updatedGame = new GameData(command.getGameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);
        gameAccess.updateGame(updatedGame);
        LoadGameMessage gameMessage = new LoadGameMessage(updatedGame);
        String game = new Gson().toJson(gameMessage);
        connections.broadcast("none121", game);
        NotificationMessage notification = new NotificationMessage(authData.username() + " has made a move");
        String serverMessage = new Gson().toJson(notification);
        connections.broadcast(authData.username(), serverMessage);
    }
}