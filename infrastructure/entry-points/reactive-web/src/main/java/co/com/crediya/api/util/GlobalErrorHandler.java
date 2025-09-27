package co.com.crediya.api.util;


import co.com.crediya.api.dto.ApiErrorDTO;
import co.com.crediya.api.exception.MissingInvalidAuthHeaderException;
import co.com.crediya.api.exception.ValidationException;
import co.com.crediya.usecase.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(ValidationException ex) {
        return Mono.just(ResponseEntity.badRequest().body(Map.of(
                "status", ex.getStatus(),
                "error", "Los datos de entrada están incompletos o inválidos.",
                "errors", ex.getErrors(),
                "timestamp", LocalDateTime.now().toString()
        )));
    }

    @ExceptionHandler({EmailAlreadyExistsException.class, IdentificationAlreadyExistsException.class})
    public Mono<ResponseEntity<ApiErrorDTO>> handleBusinessValidationExceptions(RuntimeException ex) {
        return Mono.just(ResponseEntity.badRequest().body(new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Existent unique data",
                ex.getMessage(),
                LocalDateTime.now().toString()
        )));
    }

    @ExceptionHandler({UserNotFoundException.class, RoleNotFoundException.class, MissingInvalidAuthHeaderException.class})
    public Mono<ResponseEntity<ApiErrorDTO>> handleNotFound(RuntimeException ex) {
        return Mono.just(ResponseEntity.badRequest().body(new ApiErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now().toString()
        )));
    }

    @ExceptionHandler({InvalidCredentialsException.class})
    public Mono<ResponseEntity<ApiErrorDTO>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return Mono.just(ResponseEntity.badRequest().body(new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Credentials",
                ex.getMessage(),
                LocalDateTime.now().toString()
        )));
    }


    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwt(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Token expired",
                        "message", ex.getMessage()
                ));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Bad Request",
                        "message", ex.getMessage()
                ));
    }


    @ExceptionHandler({JwtException.class, InvalidTokenException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidJwt(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Invalid token",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                ));
    }
}