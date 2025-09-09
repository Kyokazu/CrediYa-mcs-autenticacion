package co.com.crediya.r2dbc;

import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.gateways.RoleRepository;
import co.com.crediya.r2dbc.entity.RoleEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class RoleRepositoryAdapter extends ReactiveAdapterOperations<Role, RoleEntity, UUID, RoleReactiveRepository> implements RoleRepository {

    private static final Logger log = LoggerFactory.getLogger(RoleRepositoryAdapter.class);

    public RoleRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Role.class));
    }

    @Override
    public Mono<UUID> findRoleIdByName(String roleName) {
        Role role = Role.builder()
                .name(roleName)
                .build();

        return super.findByExample(role)
                .next()
                .map(Role::getId)
                .doOnSuccess(id -> log.info("Get the Role ID with the name 📌 RoleName= {} -> RoleId= {}", roleName, id));
    }

    @Override
    public Mono<String> findRoleNameById(UUID roleId) {
        Role role = Role.builder()
                .id(roleId)
                .build();
        return super.findByExample(role)
                .next()
                .map(Role::getName)
                .doOnSuccess(id -> log.info("Get the Role Name with the ID 📌 RoleName= {} -> RoleId= {}", roleId, id));
    }

}

