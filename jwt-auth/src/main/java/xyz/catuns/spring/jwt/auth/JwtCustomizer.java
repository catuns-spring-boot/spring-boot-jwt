package xyz.catuns.spring.jwt.auth;

import io.jsonwebtoken.JwtBuilder;

@FunctionalInterface
public interface JwtCustomizer {

    void customize(JwtBuilder jwt);

    static  JwtCustomizer withDefaults() {
        return (t) -> {
        };
    }
}
