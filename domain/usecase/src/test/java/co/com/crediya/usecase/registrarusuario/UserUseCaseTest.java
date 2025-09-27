package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.exception.EmailAlreadyExistsException;
import co.com.crediya.usecase.exception.IdentificationAlreadyExistsException;
import co.com.crediya.usecase.exception.RoleNotFoundException;
import co.com.crediya.usecase.exception.UserNotFoundException;
import co.com.crediya.usecase.usuario.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_IDENTIFICATION = "12345678";
    private static final String ROLE_NAME = "CLIENT";
    private static final UUID ROLE_ID = UUID.randomUUID();

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email(VALID_EMAIL)
                .identification(VALID_IDENTIFICATION)
                .build();
    }

    // ================== save(User, roleName) ==================
    @Test
    void shouldSaveUserSuccessfully() {
        when(userRepository.existingEmail(VALID_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.existingIdentification(VALID_IDENTIFICATION)).thenReturn(Mono.just(false));
        when(roleRepository.findRoleIdByName(ROLE_NAME)).thenReturn(Mono.just(ROLE_ID));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(userUseCase.save(sampleUser))
                .assertNext(user -> {
                    assertEquals(VALID_EMAIL, user.getEmail());
                    assertEquals(VALID_IDENTIFICATION, user.getIdentification());
                    assertEquals(ROLE_ID, user.getRoleId());
                })
                .verifyComplete();

        verify(userRepository).existingEmail(VALID_EMAIL);
        verify(userRepository).existingIdentification(VALID_IDENTIFICATION);
        verify(roleRepository).findRoleIdByName(ROLE_NAME);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        when(userRepository.existingEmail(VALID_EMAIL)).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.save(sampleUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof EmailAlreadyExistsException &&
                                throwable.getMessage().equals("The email is already in use"))
                .verify();
        verify(userRepository).existingEmail(VALID_EMAIL);
        verify(userRepository, never()).existingIdentification(anyString());
        verify(roleRepository, never()).findRoleIdByName(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenIdentificationAlreadyExists() {
        when(userRepository.existingEmail(VALID_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.existingIdentification(VALID_IDENTIFICATION)).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.save(sampleUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof IdentificationAlreadyExistsException &&
                                throwable.getMessage().equals("The identification is already in use"))
                .verify();

        verify(userRepository).existingEmail(VALID_EMAIL);
        verify(userRepository).existingIdentification(VALID_IDENTIFICATION);
        verifyNoInteractions(roleRepository);
    }

    @Test
    void shouldFailWhenRoleNotFound() {
        when(userRepository.existingEmail(VALID_EMAIL)).thenReturn(Mono.just(false));
        when(userRepository.existingIdentification(VALID_IDENTIFICATION)).thenReturn(Mono.just(false));
        when(roleRepository.findRoleIdByName(ROLE_NAME)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.save(sampleUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof RoleNotFoundException &&
                                throwable.getMessage().equals("Role not found: " + ROLE_NAME))
                .verify();

        verify(userRepository).existingEmail(VALID_EMAIL);
        verify(userRepository).existingIdentification(VALID_IDENTIFICATION);
        verify(roleRepository).findRoleIdByName(ROLE_NAME);
    }

    // ================== findByEmail(String) ==================
    @Test
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(userUseCase.findByEmail(VALID_EMAIL))
                .assertNext(user -> assertEquals(VALID_EMAIL, user.getEmail()))
                .verifyComplete();

        verify(userRepository).findByEmail(VALID_EMAIL);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.findByEmail(VALID_EMAIL))
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository).findByEmail(VALID_EMAIL);
    }

    // ================== findByIdentification(String) ==================
    @Test
    void shouldFindUserByIdentification() {
        when(userRepository.findByIdentification(VALID_IDENTIFICATION)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(userUseCase.findByIdentification(VALID_IDENTIFICATION))
                .assertNext(user -> assertEquals(VALID_IDENTIFICATION, user.getIdentification()))
                .verifyComplete();

        verify(userRepository).findByIdentification(VALID_IDENTIFICATION);
    }

    @Test
    void shouldFailWhenUserNotFoundByIdentification() {
        when(userRepository.findByIdentification(VALID_IDENTIFICATION)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.findByIdentification(VALID_IDENTIFICATION))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                                throwable.getMessage().equals("User not found with identification: " + VALID_IDENTIFICATION))
                .verify();

        verify(userRepository).findByIdentification(VALID_IDENTIFICATION);
    }
}
