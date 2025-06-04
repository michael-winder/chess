package client;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import requests.CreateRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.CreateResponse;
import responses.LoginResponse;
import responses.RegisterResponse;
import server.Server;
import server.ServerFacade;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ServerFacadeTests {
    static UserSQLAccess userSQLAccess ;
    private static Server server;
    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private final UserSQLTest userTest = new UserSQLTest();
    private final AuthSQLTest authTest = new AuthSQLTest();
    private final GameSQLTest gameTest = new GameSQLTest();


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
        RegisterResponse registerResponse= registerUser("Michael", "pass", "mail");
        List<String> users = userTest.loadUsers();
        assertTrue(users.contains("Michael"));
        assertEquals("Michael", registerResponse.username());
    }

    @Test
    public void failedRegister() throws ResponseException{
        assertThrows(Exception.class, ()->serverFacade.register(null));
    }

    @Test
    public void clear() throws DataAccessException{
        registerUser("Jake", "password", "email");
        serverFacade.clear();
        List<String> users = userTest.loadUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    public void loginSuccess() throws DataAccessException, ResponseException{
        RegisterResponse registerResponse = registerUser("Mike", "pass", "mail");
        LoginResponse loginResponse = loginUser("Mike", "pass");
        HashMap <String, String> auths = authTest.loadAuths();
        assertTrue(auths.containsKey(loginResponse.authToken()));
        assertTrue(auths.containsValue("Mike"));
    }

    @Test
    public void loginFail() throws ResponseException{
        registerUser("Mike","password","mail");
        assertThrows(Exception.class, () -> loginUser("Milk","password"));
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        RegisterResponse registerResponse = registerUser("Jake", "pass", "email");
        serverFacade.logout(registerResponse.authToken());
        HashMap <String, String> auths = authTest.loadAuths();
        assertFalse(auths.containsValue(registerResponse.authToken()));
    }

    @Test
    public void logoutFail() throws DataAccessException {
        registerUser("BigMike", "password", "email");
        assertThrows(Exception.class, () -> serverFacade.logout("wrong"));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        RegisterResponse registerResponse = registerUser("Jake", "pass", "email");
        LoginResponse loginResponse = loginUser("Jake", "pass");
        CreateResponse createResponse = create("game1", loginResponse.authToken());
        HashMap<Integer, GameData> games = gameTest.loadGames();
        assertTrue(games.containsKey(createResponse.gameID()));
        assertEquals("game1", games.get(createResponse.gameID()).gameName());
    }

    @Test
    public void createGameFail() throws DataAccessException {
        RegisterResponse registerResponse = registerUser("Jake", "pass", "email");
        LoginResponse loginResponse = loginUser("Jake", "pass");
        assertThrows(Exception.class, () -> create("game1", "wrongToken"));
    }

    private RegisterResponse registerUser(String name, String password, String email){
        RegisterRequest request = new RegisterRequest(name, password, email);
        return serverFacade.register(request);
    }

    private LoginResponse loginUser(String name, String password){
        LoginRequest request = new LoginRequest(name, password);
        return serverFacade.login(request);
    }

    private CreateResponse create(String gameName, String authToken){
        CreateRequest request = new CreateRequest(gameName);
        return serverFacade.createGame(request, authToken);
    }

}
