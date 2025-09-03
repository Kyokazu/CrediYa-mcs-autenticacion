package co.com.crediya.usecase.usuario;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.usuario.exception.EmailAlreadyExistsException;
import co.com.crediya.usecase.usuario.exception.IdentificationAlreadyExistsException;
import co.com.crediya.usecase.usuario.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;


    public Mono<User> save(User user) {
        return userRepository.existingEmail(user.getEmail())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new EmailAlreadyExistsException("The email is already in use")))
                .then(userRepository.existingIdentification(user.getIdentification()))
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new IdentificationAlreadyExistsException("The identification is already in use")))
                .then(userRepository.save(user));

    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> findByIdentification(String identification) {
        return userRepository.findByIdentification(identification)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with identification: " + identification)));

    }

    public Mono<Boolean> existingEmail(String email) {
        return userRepository.existingEmail(email);
    }

    public Mono<Boolean> existsByIdentification(String identification) {
        return userRepository.existingIdentification(identification);
    }


}
