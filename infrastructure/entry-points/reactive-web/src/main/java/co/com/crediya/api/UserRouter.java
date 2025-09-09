package co.com.crediya.api;

import co.com.crediya.api.config.UserPath;
import co.com.crediya.api.openapi.UserApiDoc;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints related to user management")
public class UserRouter {

    private final UserPath userPath;
    private final UserHandler userHandler;


    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route()
                .POST(userPath.getUsers(), userHandler::listenSaveUser, UserApiDoc::saveUserDoc)
                .GET(userPath.getUserByIdentification(), userHandler::listenGetUserByIdentification, UserApiDoc::getUserByIdentificationDoc)
                .GET(userPath.getEmailByIdentification(), userHandler::listenGetEmailByIdentification, UserApiDoc::getEmailByIdentificationDoc)
                .GET(userPath.getUserByEmail(), userHandler::listenGetUserByEmail, UserApiDoc::getUserByEmail)
                .build();

    }
}
