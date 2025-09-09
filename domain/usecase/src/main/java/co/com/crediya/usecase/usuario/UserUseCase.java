package co.com.crediya.usecase.usuario;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.exception.EmailAlreadyExistsException;
import co.com.crediya.usecase.exception.IdentificationAlreadyExistsException;
import co.com.crediya.usecase.exception.RoleNotFoundException;
import co.com.crediya.usecase.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public Mono<User> save(User user, String roleName) {
        return userRepository.existingEmail(user.getEmail()).flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailAlreadyExistsException("The email is already in use"));
                    }
                    return userRepository.existingIdentification(user.getIdentification());
                }).flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IdentificationAlreadyExistsException("The identification is already in use"));
                    }
                    return roleRepository.findRoleIdByName(roleName).switchIfEmpty(Mono.error(new RoleNotFoundException("Role not found: " + roleName)));
                }).map(roleId ->
                        user.toBuilder()
                                .roleId(roleId)
                                .build())
                .flatMap(userRepository::save);
    }


    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> findByIdentification(String identification) {
        return userRepository.findByIdentification(identification)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with identification: " + identification)));

    }

}
