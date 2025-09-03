package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.usuario.exception.EmailAlreadyExistsException;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Juan");
    }

    @Test
    void save_ShouldSaveUser_WhenEmailDoesNotExist() {
        // Mock
        when(userRepository.existingEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        // Test
        StepVerifier.create(userUseCase.save(user))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).existingEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void save_ShouldReturnError_WhenEmailAlreadyExists() {
        // Mock
        when(userRepository.existingEmail(user.getEmail())).thenReturn(Mono.just(true));

        // Test
        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable -> throwable instanceof EmailAlreadyExistsException &&
                        throwable.getMessage().equals("The mail is already in use"))
                .verify();

        verify(userRepository).existingEmail(user.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.findByEmail(user.getEmail()))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void existingEmail_ShouldReturnBoolean() {
        when(userRepository.existingEmail(user.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.existingEmail(user.getEmail()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository).existingEmail(user.getEmail());
    }
}
