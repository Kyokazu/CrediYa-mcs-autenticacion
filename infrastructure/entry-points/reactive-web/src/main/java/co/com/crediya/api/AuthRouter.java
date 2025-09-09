package co.com.crediya.api;

import co.com.crediya.api.config.AuthPath;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints related to auth management")

public class AuthRouter {

    private final AuthHandler authHandler;
    private final AuthPath authPath;

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return route()
                .POST(authPath.getLogin(), authHandler::login)
                .GET(authPath.getValidateUser(), authHandler::validateUser)
                .build();
    }
}