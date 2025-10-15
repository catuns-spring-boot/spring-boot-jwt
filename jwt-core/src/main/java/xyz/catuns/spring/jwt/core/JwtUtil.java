package xyz.catuns.spring.jwt.core;

import io.jsonwebtoken.Claims;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;

public interface JwtUtil<T> {

    /**
     * Generates a JWT token
     * @param auth auth
     * @return jwt token
     */
    JwtToken generate(T auth);

    /**
     *
     * @param token token value
     * @return {@link T}
     */
    T validate(String token) throws TokenValidationException;


    Claims getClaims(String token);

}
