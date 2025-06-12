package client;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import requests.CreateRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.CreateResponse;
import responses.ListResponse;
import responses.LoginResponse;
import responses.RegisterResponse;
import server.Server;
import server.help.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade serverFacade;


    @BeforeAll
    public static void init() throws DataAccessException{
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterEach
    public void cleanup()throws DataAccessException{
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() {
        RegisterResponse registerResponse= registerUser("Michael", "pass", "mail");
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
        assertThrows(Exception.class, () -> loginUser("Jake", "pass"));
    }

    @Test
    public void clearFailed(){
        assertDoesNotThrow(serverFacade::clear);
    }

    @Test
    public void loginSuccess() throws DataAccessException, ResponseException{
        RegisterResponse registerResponse = registerUser("Mike", "pass", "mail");
        assertDoesNotThrow(() -> loginUser("Mike", "pass"));
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
        assertThrows(Exception.class, () -> serverFacade.listGames(registerResponse.authToken()));
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
        JoinRequest request = new JoinRequest(ChessGame.TeamColor.WHITE, createResponse.gameID());
        assertDoesNotThrow(() -> serverFacade.joinGame(request, registerResponse.authToken()));
    }

    @Test
    public void createGameFail(){
        registerUser("Jake", "pass", "email");
        loginUser("Jake", "pass");
        assertThrows(Exception.class, () -> create("game1", "wrongToken"));
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        RegisterResponse registerResponse = registerUser("Mike", "pass", "mail");
        LoginResponse loginResponse = loginUser("Mike", "pass");
        CreateResponse createResponse = create("game1", loginResponse.authToken());
        JoinRequest request = new JoinRequest(ChessGame.TeamColor.WHITE, createResponse.gameID());
        serverFacade.joinGame(request, loginResponse.authToken());
    }

    @Test
    public void joinGameFail(){
        assertThrows(Exception.class, () -> serverFacade.joinGame(null, null));
    }

    @Test
    public void listSuccess() {
        RegisterResponse registerResponse = registerUser("Jacob", "pass", "mail");
        LoginResponse loginResponse = loginUser("Jacob", "pass");
        CreateResponse createResponse = create("game1", loginResponse.authToken());
        ListResponse listResponse = serverFacade.listGames(loginResponse.authToken());
        GameData game = new GameData(createResponse.gameID(), null, null, "game1", new ChessGame());
        assertTrue(listResponse.games().contains(game));
    }

    @Test
    public void listFail() {
        assertThrows(Exception.class, () -> serverFacade.listGames("wrong"));
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
