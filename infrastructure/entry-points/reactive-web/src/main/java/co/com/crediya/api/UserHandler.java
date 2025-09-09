package co.com.crediya.api;

import co.com.crediya.api.dto.LoggedUserDTO;
import co.com.crediya.api.dto.SaveUserDTO;
import co.com.crediya.api.dto.UserInfoDTO;
import co.com.crediya.api.util.ValidatorUtil;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.auth.AuthUseCase;
import co.com.crediya.usecase.usuario.UserUseCase;
import co.com.crediya.usecase.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserHandler {

    private final UserUseCase userUseCase;
    private final AuthUseCase authUseCase;
    private final ValidatorUtil validatorUtil;
    private final TransactionalOperator txOperator;

    public Mono<ServerResponse> listenSaveUser(ServerRequest request) {
        log.info("Creating user.");
        Set<String> allowedRoles = Set.of("ADMIN", "CONSULTANT");

        return getValidatedUser(request, allowedRoles)
                .flatMap(loggedUser -> extractAndValidateBody(request, SaveUserDTO.class)
                        .map(this::buildUserFromDTO)
                        .flatMap(user -> userUseCase.save(user, loggedUser.getRoleName()))
                )
                .as(txOperator::transactional)
                .doOnNext(savedUser -> log.info("User created successfully: {}", savedUser.getEmail()))
                .flatMap(this::buildOkResponse);
    }

    public Mono<ServerResponse> listenGetUserByIdentification(ServerRequest request) {
        return getPathVariable(request, "identification")
                .flatMap(userUseCase::findByIdentification)
                .flatMap(this::buildOkResponse)
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(UserNotFoundException.class, this::handleNotFound);
    }

    public Mono<ServerResponse> listenGetEmailByIdentification(ServerRequest request) {
        return getPathVariable(request, "identification")
                .flatMap(userUseCase::findByIdentification)
                .map(user -> Map.of("email", user.getEmail()))
                .flatMap(this::buildOkResponse)
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(UserNotFoundException.class, this::handleNotFound);
    }

    public Mono<ServerResponse> listenGetUserByEmail(ServerRequest request) {
        log.info("Getting user by email.");
        return getPathVariable(request, "email")
                .flatMap(userUseCase::findByEmail)
                .map(this::toUserInfoDTO)
                .flatMap(this::buildOkResponse)
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(UserNotFoundException.class, this::handleConflict);
    }


    private Mono<String> getPathVariable(ServerRequest request, String variableName) {
        return Mono.fromCallable(() -> request.pathVariable(variableName))
                .map(String::trim)
                .filter(value -> !value.isBlank());
    }

    private <T> Mono<T> extractAndValidateBody(ServerRequest request, Class<T> bodyClass) {
        return request.bodyToMono(bodyClass)
                .flatMap(validatorUtil::validate);
    }

    private Mono<ServerResponse> buildOkResponse(Object body) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }

    private Mono<ServerResponse> handleNotFound(UserNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    private Mono<ServerResponse> handleConflict(UserNotFoundException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String message) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "error", message,
                        "status", status.value(),
                        "timestamp", LocalDateTime.now().toString()
                ));
    }

    private UserInfoDTO toUserInfoDTO(User user) {
        return UserInfoDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .income(user.getIncome())
                .build();
    }

    private User buildUserFromDTO(SaveUserDTO dto) {
        return User.builder()
                .name(dto.getName())
                .lastname(dto.getLastname())
                .identification(dto.getIdentification())
                .birthdate(dto.getBirthdate())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .income(dto.getIncome())
                .build();
    }

    private Mono<LoggedUserDTO> getValidatedUser(ServerRequest request, Set<String> allowedRoles) {
        return Mono.justOrEmpty(request.headers().firstHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .switchIfEmpty(Mono.error(new RuntimeException("Missing or invalid Authorization header")))
                .flatMap(token -> authUseCase.validateToken(token)
                        .map(userTokenInfo -> LoggedUserDTO.builder()
                                .token(userTokenInfo.getToken())
                                .email(userTokenInfo.getEmail())
                                .roleName(userTokenInfo.getRole())
                                .build()
                        )
                )
                .flatMap(loggedUser -> {
                    if (!allowedRoles.contains(loggedUser.getRoleName())) {
                        return Mono.error(new RuntimeException("Unauthorized: insufficient permissions"));
                    }
                    return Mono.just(loggedUser);
                });
    }
}
