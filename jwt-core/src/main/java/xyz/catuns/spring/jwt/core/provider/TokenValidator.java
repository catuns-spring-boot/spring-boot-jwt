package xyz.catuns.spring.jwt.core.provider;

import io.jsonwebtoken.Claims;

import java.util.function.Function;

@FunctionalInterface
public interface TokenValidator<T> extends Function<Claims, T> {

    @SuppressWarnings("unchecked")
    static <T> TokenValidator<T> identity() {
        return claims -> (T) claims;
    }

}
