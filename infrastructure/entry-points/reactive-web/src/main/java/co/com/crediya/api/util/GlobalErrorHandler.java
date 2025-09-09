package co.com.crediya.api.util;


import co.com.crediya.api.exception.ValidationException;
import co.com.crediya.usecase.exception.EmailAlreadyExistsException;
import co.com.crediya.usecase.exception.IdentificationAlreadyExistsException;
import co.com.crediya.usecase.exception.RoleNotFoundException;
import co.com.crediya.usecase.exception.UserNotFoundException;
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

    @ExceptionHandler({EmailAlreadyExistsException.class, IdentificationAlreadyExistsException.class, RoleNotFoundException.class})
    public Mono<ResponseEntity<Map<String, Object>>> handleBusinessValidationExceptions(RuntimeException ex) {
        return Mono.just(ResponseEntity.badRequest().body(Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        )));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNotFound(UserNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value(),
                "timestamp", LocalDateTime.now().toString()
        )));
    }


    // Token expirado
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwt(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Token expired",
                        "message", ex.getMessage()
                ));
    }


    // Token faltante o mal formado (lo lanzamos con RuntimeException desde el handler)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Bad Request",
                        "message", ex.getMessage()
                ));
    }


    // Token inválido (firma incorrecta, mal formado, etc.)
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJwt(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Invalid token",
                        "message", ex.getMessage()
                ));
    }

    // Otros errores genéricos
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