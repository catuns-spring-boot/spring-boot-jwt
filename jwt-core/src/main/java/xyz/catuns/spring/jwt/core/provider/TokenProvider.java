package xyz.catuns.spring.jwt.core.provider;

import io.jsonwebtoken.Claims;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;

import java.time.Instant;
import java.util.Date;


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
    Claims getClaims(String token);

    /**
     * Determines whether the token is expired
     * @param token token value
     * @return boolean
     */
    default boolean isExpired(String token) {
        Claims claims = this.getClaims(token);
        Date expiration = claims.getExpiration();
        return Instant.now().isAfter(expiration.toInstant());
    }

}
