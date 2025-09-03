package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, UUID, UserReactiveRepository> implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryAdapter.class);

    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
    }

    @Override
    public Mono<User> save(User user) {
        return super.save(user);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return null;
    }

    @Override
    public Mono<User> findByIdentification(String identification) {
        User user = User.builder()
                .identification(identification)
                .build();
        return super.findByExample(user)
                .doOnNext(exists -> log.debug("findByIdentification with param: {} -> {}", identification, exists))
                .next();
    }

    @Override
    public Mono<Boolean> existingEmail(String email) {
        User user = User.builder()
                .email(email)
                .build();
        return super.findByExample(user)
                .doOnNext(exists -> log.debug("existsByMail with param: {} -> {}", email, exists))
                .next()
                .map(u -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> existingIdentification(String identification) {
        User user = User.builder()
                .identification(identification)
                .build();
        return super.findByExample(user)
                .doOnNext(exists -> log.debug("existsByIdentification with param: {} -> {}", identification, exists))
                .next()
                .map(u -> true)
                .defaultIfEmpty(false);
    }


}
