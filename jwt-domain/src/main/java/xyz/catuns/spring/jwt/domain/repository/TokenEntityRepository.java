package xyz.catuns.spring.jwt.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import xyz.catuns.spring.jwt.domain.entity.TokenEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TokenEntityRepository<T extends TokenEntity> extends JpaRepository<T, Long> {

    Optional<T> findByIdentifierAndExpiresAfter(
            @Param("identifier") String identifier,
            @Param("expires") Instant expiration);

    List<T> findAllByIdentifier(@Param("identifier") String identifier);

    Optional<T> findByToken(String token);

    void deleteAllByIdentifier(String identifier);

    void deleteAllByIdentifierAndExpiresBefore(
            @Param("identifier") String identifier,
            @Param("expires") Instant expiration
    );
}
