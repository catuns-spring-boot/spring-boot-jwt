package xyz.catuns.spring.jwt.domain.entity;

import com.github.slugify.Slugify;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import xyz.catuns.spring.jwt.domain.utils.Slugifier;

import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class RoleEntity implements GrantedAuthority, Slugifier {

    @Id
    @Setter(value = AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "name", nullable = false, length = 50)
    protected String name;

    public void setName(String name) {
        this.name = slug(name);
    }

    @Override
    public String slug(String name) {
        return new Slugify()
                .withUnderscoreSeparator(true)
                .withLowerCase(false)
                .slugify(name)
                .toUpperCase();
    }

    @Override
    public String getAuthority() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoleEntity role = (RoleEntity) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}
