package xyz.catuns.spring.jwt.autoconfigure.annotation;

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
public @interface EnableJwtSecurity {
}
