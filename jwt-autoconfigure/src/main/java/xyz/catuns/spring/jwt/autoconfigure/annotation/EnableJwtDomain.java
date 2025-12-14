package xyz.catuns.spring.jwt.autoconfigure.annotation;

import org.springframework.context.annotation.Import;
import xyz.catuns.spring.jwt.autoconfigure.registrar.JwtDomainRegistrar;

import java.lang.annotation.*;

/**
 * <h1>Enable JWT Domain</h1>
 * <p>
 * Usage:
 * <p>
 * // Basic usage (no domain entities)
 *
 * @EnableJwtSecurity With domain entities
 * <pre>
 * @EnableJwtSecurity(
 *     entityPackages = "com.myapp.domain",
 *     userEntityClass = MyUser.class
 * )
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JwtDomainRegistrar.class)
public @interface EnableJwtDomain {

    /**
     * Packages to scan for domain entities and repositories
     * If specified, enables full domain scanning
     */
    String[] scanPackages() default {};

    /**
     * Concrete UserEntity implementation class
     * Must extend {@link xyz.catuns.spring.jwt.domain.entity.UserEntity}
     */
    Class<?> userEntityClass() default Object.class;

    /**
     * Concrete RoleEntity implementation class
     * Must extend {@link xyz.catuns.spring.jwt.domain.entity.RoleEntity}
     */
    Class<?> roleEntityClass() default Object.class;

    /**
     * Concrete UserEntityRepository implementation interface
     * Must extend {@link xyz.catuns.spring.jwt.domain.repository.UserEntityRepository}
     */
    Class<?> userRepositoryClass() default Object.class;

}
