package exception;

import com.google.gson.Gson;

import java.util.Map;

public class UnauthorizedException extends RuntimeException {
    final private int statusCode;

    public UnauthorizedException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public int statusCode() {
        return statusCode;
    }
}
