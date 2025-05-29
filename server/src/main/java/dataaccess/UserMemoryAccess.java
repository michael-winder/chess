package dataaccess;

import requests.RegisterRequest;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;

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

    public boolean verifyUser(String username, String password){
        UserData user = getUser(username);
        return (Objects.equals(user.password(), password));
    }
}
