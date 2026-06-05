package xyz.catuns.spring.jwt.core.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import xyz.catuns.spring.jwt.core.exception.JwtSecurityException;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;
import xyz.catuns.spring.jwt.core.properties.JwtMetadata;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class SimpleTokenProvider<T> implements TokenProvider<T> {

    protected final String secret;
    protected final Duration expiration;
    protected final String issuer;
    /**
     * Set customizer prior to token generation
     */
    @Setter
    protected TokenGenerator<T> customizer;
    /**
     * Set customizer prior to token generation
     */
    @Setter
    protected TokenValidator<T> validator;

    public SimpleTokenProvider(JwtMetadata jwtProperties) {
        this(jwtProperties, TokenGenerator.withDefaults(), TokenValidator.identity());
    }

    public SimpleTokenProvider(JwtMetadata jwtProperties, TokenGenerator<T> customizer, TokenValidator<T> validator) {
        this(
                jwtProperties.getSecret(),
                jwtProperties.getExpiration(),
                jwtProperties.getIssuer(),
                customizer,
                validator
        );
    }

    public SimpleTokenProvider(String secret, Duration expiration, String issuer) {
        this(secret, expiration, issuer, TokenGenerator.withDefaults(), TokenValidator.identity());
    }
    public SimpleTokenProvider(String secret, Duration expiration, String issuer, TokenGenerator<T> customizer, TokenValidator<T> validator) {
        if (secret == null || secret.isEmpty()) {
            throw new JwtSecurityException("missing secret");
        }
        this.secret = secret;
        this.expiration = expiration;
        this.issuer = issuer;
        this.customizer = customizer;
        this.validator = validator;
    }


    @Override
    public JwtToken generate(T claims) {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plus(this.expiration);
        JwtBuilder jwtBuilder = Jwts.builder()
                .issuer(this.issuer)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(this.getSecretKey());

        customizer.accept(jwtBuilder, claims);
        String token = jwtBuilder.compact();

        return new JwtToken(token, expiration, issuedAt);
    }

    @Override
    public T validate(String token) throws TokenValidationException {
        try {
            Claims claims = this.getClaims(token);
            return this.validator.apply(claims);
        } catch (Exception e) {
            throw new TokenValidationException(e);
        }
    }

    @Override
    public Claims getClaims(String token) {
        SecretKey secretKey = this.getSecretKey();
        return Jwts.parser().verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    protected SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> Builder<T> builder(String secret) {
        return new Builder<>(secret);
    }
    public static <T> Builder<T> builder(JwtMetadata properties) {
        return new Builder<>(properties);
    }

    public static final class Builder<T> {

        private final String secret;
        private Duration expiration = Duration.ofHours(10);
        private String issuer;
        private TokenGenerator<T> customizer = TokenGenerator.withDefaults();
        private TokenValidator<T> validator = TokenValidator.identity();

        private Builder(String secret) {
            if (secret == null || secret.isEmpty()) {
                throw new JwtSecurityException("missing secret");
            }
            this.secret = secret;
        }

        private Builder(JwtMetadata properties) {
            this(properties.getSecret());
            this.jwtProperties(properties);
        }

        public Builder<T> jwtProperties(JwtMetadata properties) {
            this.expiration = properties.getExpiration();
            this.issuer = properties.getIssuer();
            return this;
        }

        public Builder<T> expiration(Duration expiration) {
            this.expiration = expiration;
            return this;
        }

        public Builder<T> issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder<T> customizer(TokenGenerator<T> customizer) {
            this.customizer = customizer;
            return this;
        }

        public Builder<T> validator(TokenValidator<T> validator) {
            this.validator = validator;
            return this;
        }

        public SimpleTokenProvider<T> build() {
            return new SimpleTokenProvider<>(secret, expiration, issuer, customizer, validator);
        }
    }

}
