package xyz.catuns.spring.jwt.core.provider;

import io.jsonwebtoken.Claims;

import java.util.function.Function;

@FunctionalInterface
public interface TokenValidator<T> extends Function<Claims, T> {

    static <T> TokenValidator<T> identity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'identity'");
    }

}
