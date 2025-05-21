package dataaccess;

import requests.RegisterRequest;
import model.UserData;

import java.util.HashMap;

public class UserMemoryAccess implements UserDAO{
    final private HashMap<String, UserData> allUsers = new HashMap<>();

    public void createUser(RegisterRequest request){
        UserData user = new UserData(request.username(), request.password(), request.email());
        allUsers.put(request.username(),user);
    }

    public UserData getUser(String username){
        return allUsers.get(username);
    }

    public void deleteAllUsers(){
        allUsers.clear();
    }
}
