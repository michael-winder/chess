package exception;

import com.google.gson.Gson;

import java.util.Map;

public class AlreadyTakenException extends Exception {
    final private int statusCode;

    public AlreadyTakenException(int statusCode, String message) {
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
