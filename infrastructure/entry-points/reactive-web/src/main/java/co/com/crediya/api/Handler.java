package co.com.crediya.api;

import co.com.crediya.api.dto.SaveUserDTO;
import co.com.crediya.api.exception.ValidationException;
import co.com.crediya.api.util.ValidatorUtil;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.usuario.EmailAlreadyExistsException;
import co.com.crediya.usecase.usuario.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final ObjectMapper objectMapper;
    private final ValidatorUtil validatorUtil;
    private final TransactionalOperator txOperator;


    public Mono<ServerResponse> listenSaveUser(ServerRequest request) {
        return request.bodyToMono(SaveUserDTO.class)
                .flatMap(validatorUtil::validate)
                .map(userDTO -> objectMapper.convertValue(userDTO, User.class))
                .flatMap(userUseCase::save)
                .as(txOperator::transactional)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser))

                .onErrorResume(EmailAlreadyExistsException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of(
                                        "error", e.getMessage(),
                                        "status", HttpStatus.CONFLICT.value(),
                                        "timestamp", LocalDateTime.now().toString()
                                ))
                );
    }



}
