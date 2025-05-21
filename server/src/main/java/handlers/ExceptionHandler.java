package handlers;

import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import spark.Request;
import spark.Response;

public class ExceptionHandler {
    public static void takenHandler(AlreadyTakenException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    public static void badRequestHandler(BadRequestException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    public static void unauthorizedHandler(UnauthorizedException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }
}
