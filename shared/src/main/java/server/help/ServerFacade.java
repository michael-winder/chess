package server.help;
import com.google.gson.Gson;
import exception.ResponseException;
import requests.*;
import responses.CreateResponse;
import responses.ListResponse;
import responses.LoginResponse;
import responses.RegisterResponse;

import java.io.*;
import java.net.*;


public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public void clear(){
        makeRequest("DELETE", "/db", null, null, null);
    }

    public RegisterResponse register(RegisterRequest request){
        return makeRequest("POST", "/user", request, null, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request){
        return makeRequest("POST", "/session", request, null, LoginResponse.class);
    }

    public void logout(String authToken){
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    public ListResponse listGames(String authToken){
        return makeRequest("GET", "/game", null, authToken, ListResponse.class);
    }

    public CreateResponse createGame(CreateRequest request, String authToken){
        return makeRequest("POST", "/game", request, authToken, CreateResponse.class);
    }

    public void joinGame(JoinRequest request, String authToken){
        makeRequest("PUT", "/game", request, authToken, null);
    }



    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true); //sending a request body
            writeHeader(authToken, http);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static void writeHeader(String authToken, HttpURLConnection http){
        if (authToken != null){
            http.addRequestProperty("authorization", authToken);
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
