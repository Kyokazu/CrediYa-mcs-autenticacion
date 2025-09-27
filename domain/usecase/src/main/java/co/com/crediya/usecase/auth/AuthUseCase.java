package co.com.crediya.usecase.auth;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.UserTokenInfo;
import co.com.crediya.model.user.gateways.JwtGateway;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.exception.InvalidCredentialsException;
import co.com.crediya.usecase.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final JwtGateway jwtGateway;
    private final RoleRepository roleRepository;

    public Mono<UserTokenInfo> login(User user) {
        return userRepository.findByEmail(user.getEmail())
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Wrong email or password")))
                .flatMap(u -> validatePassword(user.getPassword(), u.getPassword())
                        .then(generateUserTokenInfo(u))
                );
    }
    
    public Mono<UserTokenInfo> validateToken(String token) {
        return jwtGateway.validateToken(token)
                .switchIfEmpty(Mono.error(new InvalidTokenException("Invalid or expired token")));
    }

    private Mono<Void> validatePassword(String inputPassword, String storedPassword) {
        if (!inputPassword.equals(storedPassword)) {
            return Mono.error(new InvalidCredentialsException("Invalid credentials"));
        }
        return Mono.empty();
    }

    private Mono<UserTokenInfo> generateUserTokenInfo(User u) {
        return roleRepository.findRoleNameById(u.getRoleId())
                .map(roleName -> {
                    User userWithAddress = u.toBuilder()
                            .address(roleName)
                            .build();
                    UserTokenInfo userTokenInfo = UserTokenInfo.builder()
                            .email(u.getEmail())
                            .role(roleName)
                            .build();
                    return userTokenInfo.toBuilder()
                            .token(jwtGateway.generateToken(userWithAddress))
                            .build();
                });
    }

}