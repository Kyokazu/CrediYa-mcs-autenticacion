package co.com.crediya.model.user.gateways;


import co.com.crediya.model.user.User;
import co.com.crediya.model.user.UserTokenInfo;
import reactor.core.publisher.Mono;

public interface JwtGateway {
    String generateToken(User user);

    Mono<UserTokenInfo> validateToken(String token);
}