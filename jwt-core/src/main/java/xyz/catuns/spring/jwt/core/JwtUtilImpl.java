package xyz.catuns.spring.jwt.core;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import xyz.catuns.spring.jwt.core.exception.MissingSecretException;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;
import xyz.catuns.spring.jwt.core.properties.JwtProperties;
import xyz.catuns.spring.jwt.core.validator.TokenValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

public class JwtUtilImpl extends AbstractJwtUtil<Authentication> {

    public static final String AUTHORITIES_CLAIM_KEY = "authorities";
    public static final String USERNAME_CLAIM_KEY = "username";

    private final String issuer;
    private final Duration expiration;

    /**
     * Set customizer prior to token generation
     */
    @Setter
    private Customizer<JwtBuilder> generationCustomizer = Customizer.withDefaults();

    /**
     * Set customizer prior to token generation
     */
    @Setter
    private TokenValidator<Claims> tokenValidator = TokenValidator.withDefaults();

    public JwtUtilImpl(String secret, String issuer, Duration expiration) throws MissingSecretException {
        super(secret);
        this.issuer = issuer;
        this.expiration = expiration;
    }

    /**
     * Generates a JWT token using {@link JwtProperties#getExpiration()} duration
     * @param auth auth
     * @return jwt token
     */
    @Override
    public JwtToken generate(Authentication auth) {
        Instant now = Instant.now();
        Instant expiration = now.plus(this.expiration);
        Set<String> authoritiesList = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        JwtBuilder jwtBuilder = Jwts.builder()
                .issuer(this.issuer)
                .subject(auth.getName())
                .claim(USERNAME_CLAIM_KEY, auth.getPrincipal())
                .claim(AUTHORITIES_CLAIM_KEY, String.join(",", authoritiesList));
        generationCustomizer.customize(jwtBuilder);
        String token = jwtBuilder.issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getSecretKey())
                .compact();

        return new JwtToken(token, expiration, now, auth.getName());
    }

    @Override
    public Authentication validate(String token) throws TokenValidationException {
        Claims claims = getClaims(token);
        String username = String.valueOf(claims.get(USERNAME_CLAIM_KEY));
        String authorities = String.valueOf(claims.get(AUTHORITIES_CLAIM_KEY));

        tokenValidator.validate(claims);
        return new UsernamePasswordAuthenticationToken(username, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
    }

}
