package websocket;
import dataaccess.AuthSQLAccess;
import dataaccess.DataAccessException;
import dataaccess.GameSQLAccess;
import dataaccess.UserSQLAccess;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private GameSQLAccess gameAccess;
    public ConnectionManager() {
        try {
            this.gameAccess = new GameSQLAccess();
        } catch (DataAccessException e) {
            throw new ResponseException(500, "unable to initialize Database");
        }
    }

    public void add(int gameID, String username, Session session) {
        var connection = new Connection(username, session, gameID);
        connections.put(username, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, String message, int gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeVisitorName) && c.gameID == gameID) {
                    c.send(message);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }
}
