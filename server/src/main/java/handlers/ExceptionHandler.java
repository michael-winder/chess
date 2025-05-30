package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import responses.ErrorResponse;
import spark.Request;
import spark.Response;

public class ExceptionHandler {
    public static void takenHandler(AlreadyTakenException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    public static void badRequestHandler(BadRequestException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    public static void unauthorizedHandler(UnauthorizedException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    public static void dataAccessHandler(DataAccessException ex, Request req, Response res){
        res.status(500);
        res.body(new Gson().toJson(
                new ErrorResponse("Error: " + ex.getMessage())));
    }
}
