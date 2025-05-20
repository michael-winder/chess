package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class AuthMemoryAccess implements AuthDAO{
    final private HashMap<String, AuthData> allAuthData = new HashMap<>();

    public void createAuth(String username){
        String authToken = AuthDAO.generateToken();
        AuthData auth = new AuthData(authToken,username);
        allAuthData.put(authToken,auth);
    }

    public AuthData getAuth(String authToken){
        return allAuthData.get(authToken);
    }
}
