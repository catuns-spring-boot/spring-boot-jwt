package xyz.catuns.spring.jwt.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.catuns.spring.jwt.domain.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository<Entity extends UserEntity> extends JpaRepository<Entity, UUID> {

    Optional<Entity> findByEmail(String email);
}