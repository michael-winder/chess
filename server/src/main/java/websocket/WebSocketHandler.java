package websocket;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.JoinNotification;
import websocket.messages.ServerMessage;
import websocket.ConnectionManager;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {
    public final UserDAO userAccess;
    public final AuthDAO authAccess;
    public final GameDAO gameAccess;
    private final ConnectionManager connections = new ConnectionManager();
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
        }
    }

    private void join(Session session, UserGameCommand command) throws IOException, DataAccessException {
        AuthData authData = authAccess.getAuth(command.getAuthToken());
        GameData gameData = gameAccess.getGame(command.getGameID());
        connections.add(authData.username(), session);
        JoinNotification notification;
        if (Objects.equals(gameData.whiteUsername(), authData.username())){
            notification = new JoinNotification(authData.username() + " has joined the game as white");
        } else if (Objects.equals(gameData.blackUsername(), authData.username())){
            notification = new JoinNotification(authData.username() + " has joined the game as black");
        } else {
            notification = new JoinNotification(authData.username() + " is observing the game");
        }
        String serverMessage = new Gson().toJson(notification);
        connections.broadcast(authData.username(), serverMessage);
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
        } else {
            return;
        }
        GameData updatedGame = new GameData(command.getGameID(), whiteUser, blackUser, gameData.gameName(), gameData.game());
        gameAccess.updateGame(updatedGame);
        connections.remove(authData.username());
        JoinNotification notification = new JoinNotification(authData.username() + " has left the game");
        String serverMessage = new Gson().toJson(notification);
        connections.broadcast(authData.username(), serverMessage);
    }
}