package xyz.catuns.spring.jwt.core.validator;

import xyz.catuns.spring.jwt.core.exception.TokenValidationException;

@FunctionalInterface
public interface TokenValidator<T> {

    void validate(T claims) throws TokenValidationException;

    static <T> TokenValidator<T> withDefaults() {
        return (t) -> {
        };
    }
}
