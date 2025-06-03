package client;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserSQLAccess;
import dataaccess.UserSQLTest;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import requests.RegisterRequest;
import server.Server;
import server.ServerFacade;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class ServerFacadeTests {
    static UserSQLAccess userSQLAccess ;
    private static Server server;
    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private final UserSQLTest userTest = new UserSQLTest();


    @BeforeAll
    public static void init() throws DataAccessException{
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        userSQLAccess = new UserSQLAccess();
    }

    @AfterEach
    public void cleanup()throws DataAccessException{
        userSQLAccess.deleteAllUsers();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() throws DataAccessException, ResponseException {
        RegisterRequest request = new RegisterRequest("Michael", "password", "myEmail");
        serverFacade.register(request);
        List<String> users = userTest.loadUsers();
        assertTrue(users.contains("Michael"));
    }

}
