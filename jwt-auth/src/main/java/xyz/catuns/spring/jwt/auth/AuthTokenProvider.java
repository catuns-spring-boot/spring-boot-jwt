package xyz.catuns.spring.jwt.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import xyz.catuns.spring.jwt.core.properties.JwtProperties;
import xyz.catuns.spring.jwt.core.provider.TokenGenerator;
import xyz.catuns.spring.jwt.core.provider.SimpleTokenProvider;
import xyz.catuns.spring.jwt.core.provider.TokenValidator;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Creates {@link Authentication} from secret string
 * <p>
 * Override {@link AuthTokenProvider#setCustomizer(TokenGenerator)} to extend generator
 * Override {@link AuthTokenProvider#setValidator(TokenValidator)} to extend validator
 */
public class AuthTokenProvider extends SimpleTokenProvider<Authentication> {

    public static final String AUTHORITIES_CLAIM_KEY = "authorities";
    public static final String USER_CLAIM_KEY = "user";

    public static TokenValidator<Authentication> defaultTokenValidator() {
        return (claims) -> {
            String username = String.valueOf(claims.getSubject());
            String authorities = String.valueOf(claims.get(AUTHORITIES_CLAIM_KEY));
            if (username == null || username.isEmpty()) {
                throw new IllegalArgumentException("missing username in claims");
            }
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
            return new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
        };
    }

    public static TokenGenerator<Authentication> defaultTokenGenerator() {
        return (jwt, auth) -> {
            Set<String> authoritiesList = AuthorityUtils.authorityListToSet(auth.getAuthorities());
            jwt.subject(auth.getName())
                    .claim(USER_CLAIM_KEY, auth.getName())
                    .claim(AUTHORITIES_CLAIM_KEY, String.join(",", authoritiesList));
        };
    }


    public AuthTokenProvider(String secret, Duration expiration, String issuer, TokenGenerator<Authentication> customizer, TokenValidator<Authentication> validator) {
        super(secret, expiration, issuer, customizer, validator);
    }

    public AuthTokenProvider(JwtProperties properties) {
        this(properties, defaultTokenGenerator(), defaultTokenValidator());
    }
    public AuthTokenProvider(JwtProperties properties, TokenGenerator<Authentication> customizer, TokenValidator<Authentication> validator) {
        super(properties, customizer, validator);
    }


    public AuthTokenProvider(String secret, String issuer, Duration expiration) {
        super(secret, expiration, issuer, defaultTokenGenerator(), defaultTokenValidator());
    }
}
