package xyz.catuns.spring.jwt.autoconfigure.annotation;

import org.springframework.context.annotation.Import;
import xyz.catuns.spring.jwt.autoconfigure.JwtAuthenticationAutoConfiguration;
import xyz.catuns.spring.jwt.autoconfigure.JwtSecurityAutoConfiguration;

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
@Import({
        JwtSecurityAutoConfiguration.class,
        JwtAuthenticationAutoConfiguration.class
})
public @interface EnableJwtSecurity {
}
