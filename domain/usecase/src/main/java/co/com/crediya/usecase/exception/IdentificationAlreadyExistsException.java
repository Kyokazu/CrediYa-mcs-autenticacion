package co.com.crediya.usecase.exception;

public class IdentificationAlreadyExistsException extends RuntimeException {
    public IdentificationAlreadyExistsException(String message) {
        super(message);
    }
}