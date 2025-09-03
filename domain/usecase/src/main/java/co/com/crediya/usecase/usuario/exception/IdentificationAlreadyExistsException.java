package co.com.crediya.usecase.usuario.exception;

public class IdentificationAlreadyExistsException extends RuntimeException {
    public IdentificationAlreadyExistsException(String message) {
        super(message);
    }
}