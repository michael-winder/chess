package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class AuthMemoryAccess implements AuthDAO{
    final private HashMap<String, AuthData> allAuthData = new HashMap<>();

    public String createAuth(String username){
        String authToken = AuthDAO.generateToken();
        AuthData auth = new AuthData(authToken,username);
        allAuthData.put(authToken,auth);
        return authToken;
    }

    public AuthData getAuth(String authToken){
        return allAuthData.get(authToken);
    }

    public void deleteAllAuth(){
        allAuthData.clear();
    }

    public void deleteAuth(String authToken){
        allAuthData.remove(authToken);
    }
}
