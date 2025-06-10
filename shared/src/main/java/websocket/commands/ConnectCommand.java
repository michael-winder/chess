package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    public boolean join;
    public String username;

    public ConnectCommand(String authToken, Integer gameID, Boolean join, String username){
        super(CommandType.CONNECT, authToken, gameID);
        this.join = join;
        this.username = username;
    }
}
