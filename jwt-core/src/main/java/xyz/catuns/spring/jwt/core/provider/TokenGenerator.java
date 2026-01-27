package xyz.catuns.spring.jwt.core.provider;

import io.jsonwebtoken.JwtBuilder;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface TokenGenerator<T> extends BiConsumer<JwtBuilder, T> {

    static <T> TokenGenerator<T> withDefaults() {
        return (jwt, t) -> {};
    }
}
