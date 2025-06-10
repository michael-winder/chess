package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
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
        ConnectCommand command = new Gson().fromJson(message, ConnectCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> enter(session, command);
        }
    }

    private void enter(Session session, ConnectCommand command) throws IOException {
        connections.add(command.username, session);
        var notification = new JoinNotification(command.username + " has joined the game");
        String message = new Gson().toJson(notification);
        connections.broadcast(command.username, message);
    }
}