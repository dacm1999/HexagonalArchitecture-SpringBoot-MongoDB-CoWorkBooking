package com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository;

import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;

public interface RepositorioProducto {
    void deleteByUsername(String username);
    boolean existsByEmail(String email);
    UserEntity findByUsername(String username);
    boolean existsByUsername(String username);
}
