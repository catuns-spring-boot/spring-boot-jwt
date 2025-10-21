package xyz.catuns.spring.jwt.core;

import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;

import java.util.Map;


public interface TokenProvider<T> {

    /**
     * Generates a JWT token
     * @param claims claims
     * @return {@link JwtToken} jwt token
     */
    JwtToken generate(T claims);

    /**
     *
     * @param token token value
     * @return {@link T} the origin type
     */
    T validate(String token) throws TokenValidationException;

    /**
     * Extract all claims from the token.
     *
     * @param token JWT token
     * @return claims as a Map
     */
    Map<String, Object> getClaims(String token);

    /**
     * Determines whether the token is expired
     * @param token token value
     * @return boolean
     */
    boolean isExpired(String token);

}
