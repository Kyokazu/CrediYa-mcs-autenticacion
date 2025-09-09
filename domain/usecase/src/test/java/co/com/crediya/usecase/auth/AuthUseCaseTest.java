package co.com.crediya.usecase.auth;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.UserTokenInfo;
import co.com.crediya.model.user.gateways.JwtGateway;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUseCase Tests")
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtGateway jwtGateway;

    @InjectMocks
    private AuthUseCase authUseCase;

    private static final String VALID_EMAIL = "user@example.com";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrongPass";
    private static final String TOKEN = "valid-token";

    private UUID ROLE_ID;

    private User user;
    private UserTokenInfo userTokenInfo;

    @BeforeEach
    void setUp() {
        ROLE_ID = UUID.randomUUID();

        user = User.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .roleId(ROLE_ID)
                .build();

        userTokenInfo = UserTokenInfo.builder()
                .email(VALID_EMAIL)
                .role(ROLE_ID.toString())
                .token(TOKEN)
                .build();
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Mono.just(user));
        when(roleRepository.findRoleNameById(ROLE_ID)).thenReturn(Mono.just("CLIENT"));
        when(jwtGateway.generateToken(any(User.class))).thenReturn(TOKEN);

        StepVerifier.create(authUseCase.login(user))
                .assertNext(tokenInfo -> {
                    assert tokenInfo.getEmail().equals(VALID_EMAIL);
                    assert tokenInfo.getRole().equals("CLIENT");
                    assert tokenInfo.getToken().equals(TOKEN);
                })
                .verifyComplete();

        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(roleRepository).findRoleNameById(ROLE_ID);
        verify(jwtGateway).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should fail login when email not found")
    void shouldFailLoginWhenEmailNotFound() {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.login(user))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Invalid credentials"))
                .verify();

        verify(userRepository).findByEmail(VALID_EMAIL);
        verifyNoInteractions(roleRepository, jwtGateway);
    }

    @Test
    @DisplayName("Should fail login when password is invalid")
    void shouldFailLoginWhenPasswordInvalid() {
        User wrongPasswordUser = user.toBuilder().password(INVALID_PASSWORD).build();
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Mono.just(user));
        when(roleRepository.findRoleNameById(any(UUID.class))).thenReturn(Mono.just("CLIENT"));

        StepVerifier.create(authUseCase.login(wrongPasswordUser))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Invalid credentials"))
                .verify();

        verify(userRepository).findByEmail(VALID_EMAIL);
        verifyNoInteractions(jwtGateway);
    }


    @Test
    @DisplayName("Should validate token successfully")
    void shouldValidateTokenSuccessfully() {
        when(jwtGateway.validateToken(TOKEN)).thenReturn(Mono.just(userTokenInfo));
        when(roleRepository.findRoleNameById(ROLE_ID)).thenReturn(Mono.just("CLIENT"));

        StepVerifier.create(authUseCase.validateToken(TOKEN))
                .assertNext(info -> {
                    assert info.getEmail().equals(VALID_EMAIL);
                    assert info.getRole().equals("CLIENT");
                    assert info.getToken().equals(TOKEN);
                })
                .verifyComplete();

        verify(jwtGateway).validateToken(TOKEN);
        verify(roleRepository).findRoleNameById(ROLE_ID);
    }

    @Test
    @DisplayName("Should fail validateToken when token is invalid")
    void shouldFailValidateTokenWhenInvalid() {
        when(jwtGateway.validateToken(TOKEN)).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.validateToken(TOKEN))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Invalid or expired token"))
                .verify();

        verify(jwtGateway).validateToken(TOKEN);
        verifyNoInteractions(roleRepository);
    }

    @Test
    @DisplayName("Should fail validateToken when role not found")
    void shouldFailValidateTokenWhenRoleNotFound() {
        when(jwtGateway.validateToken(TOKEN)).thenReturn(Mono.just(userTokenInfo));
        when(roleRepository.findRoleNameById(ROLE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.validateToken(TOKEN))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Invalid or expired token"))
                .verify();

        verify(jwtGateway).validateToken(TOKEN);
        verify(roleRepository).findRoleNameById(ROLE_ID);
    }
}