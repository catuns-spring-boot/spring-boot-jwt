package xyz.catuns.spring.jwt.domain;

import lombok.Getter;
import xyz.catuns.spring.jwt.domain.entity.RoleEntity;
import xyz.catuns.spring.jwt.domain.entity.UserEntity;
import xyz.catuns.spring.jwt.domain.repository.UserEntityRepository;

/**
 * Bean that holds domain configuration metadata
 * Created by JwtDomainConfiguration when @EnableJwtSecurity is used with domain entities
 * Used by JwtAuthenticationAutoConfiguration for conditional bean creation
 */
@Getter
public class DomainMetadata {

    private final Class<?> userEntityClass;
    private final Class<?> roleEntityClass;
    private final Class<?> userRepositoryClass;
    private final String[] scanPackages;

    public DomainMetadata(
            Class<?> userEntityClass,
            Class<?> roleEntityClass,
            Class<?> userRepositoryClass,
            String[] scanPackages
    ) {

        this.userEntityClass = userEntityClass;
        this.roleEntityClass = roleEntityClass;
        this.userRepositoryClass = userRepositoryClass;
        this.scanPackages = scanPackages;
    }

    public boolean hasDomain() {
        return hasScanPackages() ||
                hasUserEntity() ||
                hasUserRepository();
    }

    public boolean hasUserRepository() {
        return userRepositoryClass != null &&
                userRepositoryClass != Object.class &&
                UserEntityRepository.class.isAssignableFrom(userRepositoryClass);

    }

    public boolean hasRoleEntity() {
        return roleEntityClass != null &&
                roleEntityClass != Object.class &&
                RoleEntity.class.isAssignableFrom(roleEntityClass);
    }

    public boolean hasUserEntity() {
        return userEntityClass != null &&
                userEntityClass != Object.class &&
                UserEntity.class.isAssignableFrom(userEntityClass);
    }

    public boolean hasScanPackages() {
        return scanPackages.length > 0;
    }
}
