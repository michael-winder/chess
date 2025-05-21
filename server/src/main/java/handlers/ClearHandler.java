package handlers;

import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    private final ClearService clearService = new ClearService();

    public Object clearAll(Request req, Response res){
        clearService.clear();
        return "{}";
    }
}
