package xyz.catuns.spring.jwt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(name = "identifier", nullable = false)
    protected String identifier;

    @Column(name = "token", nullable = false, unique = true)
    protected String token;

    @Column(name = "expires", nullable = false)
    protected Instant expires;

    @Column(name = "issued_at", nullable = false)
    protected Instant issuedAt;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenEntity that = (TokenEntity) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, token);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expires);
    }
}
