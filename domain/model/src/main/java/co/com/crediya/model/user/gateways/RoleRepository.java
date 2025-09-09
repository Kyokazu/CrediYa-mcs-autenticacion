package co.com.crediya.model.user.gateways;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository {
    Mono<UUID> findRoleIdByName(String roleName);

    Mono<String> findRoleNameById(UUID roleId);
}
