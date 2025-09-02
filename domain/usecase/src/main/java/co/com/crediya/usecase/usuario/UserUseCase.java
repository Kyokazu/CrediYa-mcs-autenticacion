package co.com.crediya.usecase.usuario;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;


    public Mono<User> save(User user) {
        return userRepository.existingEmail(user.getEmail())
                .flatMap(existingUser -> {
                    if (Boolean.TRUE.equals(existingUser))
                        return Mono.error(new EmailAlreadyExistsException("The mail is already in use"));
                    return userRepository.save(user);
                });
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<Boolean> existingEmail(String email) {
        return userRepository.existingEmail(email);
    }


}
