package com.biniyogbuddy.auth.service;

import com.biniyogbuddy.users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleCacheService {

    private static final String ROLES_HASH_KEY = "roles";

    private final RoleRepository roleRepository;
    private final StringRedisTemplate redisTemplate;

    public void loadRoles() {
        roleRepository.findAll().forEach(role ->
                redisTemplate.opsForHash().put(ROLES_HASH_KEY, role.getId().toString(), role.getName())
        );
    }

    public String getRoleName(Long roleId) {
        Object name = redisTemplate.opsForHash().get(ROLES_HASH_KEY, roleId.toString());
        return name != null ? name.toString() : null;
    }
}
