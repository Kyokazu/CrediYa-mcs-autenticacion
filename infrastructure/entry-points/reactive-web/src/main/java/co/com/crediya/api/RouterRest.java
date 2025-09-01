package co.com.crediya.api;

import co.com.crediya.api.config.UserPath;
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
public class RouterRest {

    private final UserPath userPath;
    private final Handler userHandler;


    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                .POST(userPath.getUsers(), userHandler::listenSaveUser)
                .build();

    }
}
