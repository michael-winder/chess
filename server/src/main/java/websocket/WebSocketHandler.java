package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
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
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> join(session, message);
            case LEAVE -> leave(session, message);
        }
    }

    private void join(Session session, String message) throws IOException {
        ConnectCommand command = new Gson().fromJson(message, ConnectCommand.class);
        connections.add(command.username, session);
        JoinNotification notification;
        if (command.join){
            notification = new JoinNotification(command.username + " has joined the game as " + command.color);
        } else {
            notification = new JoinNotification(command.username + " is observing the game");
        }
        String serverMessage = new Gson().toJson(notification);
        connections.broadcast(command.username, serverMessage);
    }

    private void leave(Session session,  String message) throws IOException {
        LeaveCommand command = new Gson().fromJson(message, LeaveCommand.class);
        connections.remove(command.username);
        JoinNotification notification = new JoinNotification(command.username + " has left the game");
        String serverMessage = new Gson().toJson(notification);
        connections.broadcast(command.username, serverMessage);
    }
}