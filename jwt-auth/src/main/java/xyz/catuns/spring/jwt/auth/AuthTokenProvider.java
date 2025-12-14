package xyz.catuns.spring.jwt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import xyz.catuns.spring.jwt.core.exception.MissingSecretException;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;
import xyz.catuns.spring.jwt.core.validator.TokenValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

/**
 * Creates {@link Authentication} from secret string
 * <p>
 * Override {@link AuthTokenProvider#setCustomizer(JwtCustomizer)} to extend generator
 * Override {@link AuthTokenProvider#setValidator(TokenValidator)} to extend validator
 */
public class AuthTokenProvider extends AbstractTokenProvider<Authentication> {

    public static final String AUTHORITIES_CLAIM_KEY = "authorities";
    public static final String USER_CLAIM_KEY = "user";

    private final String issuer;

    /**
     * Set customizer prior to token generation
     */
    @Setter
    private TokenValidator<Claims> validator = TokenValidator.withDefaults();

    public AuthTokenProvider(String secret, String issuer, Duration expiration) throws MissingSecretException {
        super(secret, expiration);
        this.issuer = issuer;
        this.setCustomizer((jwt, auth) -> {
            Set<String> authoritiesList = AuthorityUtils.authorityListToSet(auth.getAuthorities());
            jwt.issuer(this.issuer)
                .subject(auth.getName())
                .claim(USER_CLAIM_KEY, auth.getPrincipal())
                .claim(AUTHORITIES_CLAIM_KEY, String.join(",", authoritiesList));
        });
    }

    @Override
    public Authentication validate(String token) throws TokenValidationException {
        Claims claims = getClaims(token);
        String username = String.valueOf(claims.get(USER_CLAIM_KEY));
        String authorities = String.valueOf(claims.get(AUTHORITIES_CLAIM_KEY));
        validator.validate(claims);
        return new UsernamePasswordAuthenticationToken(username, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
    }

}
