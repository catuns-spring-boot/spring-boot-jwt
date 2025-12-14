package xyz.catuns.spring.jwt.auth;

import io.jsonwebtoken.JwtBuilder;

@FunctionalInterface
public interface JwtCustomizer<T> {

    void customize(JwtBuilder jwt, T t);

    static <T> JwtCustomizer<T> withDefaults() {
        return (jwt, t) -> {
        };
    }
}
