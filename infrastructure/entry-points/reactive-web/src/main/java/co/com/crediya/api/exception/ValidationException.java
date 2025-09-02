package co.com.crediya.api.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final int status;

    public ValidationException(String message, int status) {
        super(message);
        this.status = status;
    }

}