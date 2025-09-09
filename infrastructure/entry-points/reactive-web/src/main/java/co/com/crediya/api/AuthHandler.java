package co.com.crediya.api;

import co.com.crediya.api.dto.LoggedUserDTO;
import co.com.crediya.api.dto.LoginDTO;
import co.com.crediya.api.util.ValidatorUtil;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.auth.AuthUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthHandler {

    private final AuthUseCase authUseCase;
    private final ValidatorUtil validatorUtil;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginDTO.class)
                .flatMap(validatorUtil::validate)
                .map(dto -> User.builder()
                        .email(dto.getEmail())
                        .password(dto.getPassword())
                        .build()
                )
                .flatMap(authUseCase::login)
                .flatMap(userTokenInfo ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userTokenInfo)
                );
    }

    public Mono<ServerResponse> validateUser(ServerRequest request) {
        return Mono.justOrEmpty(request.headers().firstHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .switchIfEmpty(Mono.error(new RuntimeException("Missing or invalid Authorization header")))
                .map(header -> header.substring(7))
                .flatMap(token ->
                        authUseCase.validateToken(token)
                                .map(userTokenInfo -> LoggedUserDTO.builder()
                                        .token(userTokenInfo.getToken())
                                        .email(userTokenInfo.getEmail())
                                        .roleName(userTokenInfo.getRole())
                                        .build()
                                )
                )
                .flatMap(loggedUserDTO ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(loggedUserDTO)
                );
    }
}