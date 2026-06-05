package xyz.catuns.spring.jwt.core.provider;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import xyz.catuns.spring.jwt.core.exception.JwtSecurityException;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;
import xyz.catuns.spring.jwt.core.properties.JwtMetadata;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimpleTokenProviderTest {

    static final String SECRET = "test-secret-key-at-least-32-bytes-long!!";

    @Nested
    class BuilderTest {

        @Test
        void buildsWithSecretOnly() {
            SimpleTokenProvider<Claims> provider = SimpleTokenProvider.<Claims>builder(SECRET).build();
            assertThat(provider).isNotNull();
        }

        @Test
        void nullSecret_throwsJwtSecurityException() {
            assertThatThrownBy(() -> SimpleTokenProvider.builder((String) null))
                    .isInstanceOf(JwtSecurityException.class);
        }

        @Test
        void emptySecret_throwsJwtSecurityException() {
            assertThatThrownBy(() -> SimpleTokenProvider.builder(""))
                    .isInstanceOf(JwtSecurityException.class);
        }

        @Test
        void jwtProperties_appliesExpirationAndIssuer() {
            JwtMetadata props = new JwtMetadata();
            props.setIssuer("props-issuer");
            props.setExpiration(Duration.ofMinutes(30));

            SimpleTokenProvider<Claims> provider = SimpleTokenProvider.<Claims>builder(SECRET)
                    .jwtProperties(props)
                    .build();

            assertThat(provider.expiration).isEqualTo(Duration.ofMinutes(30));
            assertThat(provider.issuer).isEqualTo("props-issuer");
        }

        @Test
        void explicitExpiration_overridesDefault() {
            Duration custom = Duration.ofSeconds(90);
            SimpleTokenProvider<Claims> provider = SimpleTokenProvider.<Claims>builder(SECRET)
                    .expiration(custom)
                    .build();
            assertThat(provider.expiration).isEqualTo(custom);
        }

        @Test
        void defaultExpiration_isTenHours() {
            SimpleTokenProvider<Claims> provider = SimpleTokenProvider.<Claims>builder(SECRET).build();
            assertThat(provider.expiration).isEqualTo(Duration.ofHours(10));
        }
    }

    @Nested
    class GenerateTest {

        SimpleTokenProvider<String> provider;

        @BeforeEach
        void setUp() {
            provider = SimpleTokenProvider.<String>builder(SECRET)
                    .expiration(Duration.ofMinutes(5))
                    .issuer("test-issuer")
                    .customizer((jwt, subject) -> jwt.subject(subject))
                    .validator(Claims::getSubject)
                    .build();
        }

        @Test
        void returnsTokenWithNonBlankValue() {
            JwtToken token = provider.generate("alice");
            assertThat(token.value()).isNotBlank();
        }

        @Test
        void expirationIsInTheFuture() {
            JwtToken token = provider.generate("alice");
            assertThat(token.expiration()).isAfter(Instant.now());
        }

        @Test
        void issuedAtIsPopulated() {
            JwtToken token = provider.generate("alice");
            assertThat(token.issuedAt()).isBeforeOrEqualTo(Instant.now());
        }
    }

    @Nested
    class ValidateTest {

        SimpleTokenProvider<String> provider;

        @BeforeEach
        void setUp() {
            provider = SimpleTokenProvider.<String>builder(SECRET)
                    .expiration(Duration.ofMinutes(5))
                    .issuer("test-issuer")
                    .customizer((jwt, subject) -> jwt.subject(subject))
                    .validator(Claims::getSubject)
                    .build();
        }

        @Test
        void roundTrip_returnsSubject() throws TokenValidationException {
            JwtToken token = provider.generate("alice");
            assertThat(provider.validate(token.value())).isEqualTo("alice");
        }

        @Test
        void getClaims_subject() {
            JwtToken token = provider.generate("bob");
            assertThat(provider.getClaims(token.value()).getSubject()).isEqualTo("bob");
        }

        @Test
        void getClaims_issuer() {
            JwtToken token = provider.generate("bob");
            assertThat(provider.getClaims(token.value()).getIssuer()).isEqualTo("test-issuer");
        }

        @Test
        void tamperedToken_throwsTokenValidationException() {
            JwtToken token = provider.generate("alice");
            assertThatThrownBy(() -> provider.validate(token.value() + "x"))
                    .isInstanceOf(TokenValidationException.class);
        }

        @Test
        void tokenFromDifferentSecret_throwsTokenValidationException() {
            SimpleTokenProvider<String> other = SimpleTokenProvider.<String>builder("different-secret-key-at-least-32-bytes!")
                    .customizer((jwt, subject) -> jwt.subject(subject))
                    .validator(Claims::getSubject)
                    .build();

            JwtToken token = other.generate("alice");
            assertThatThrownBy(() -> provider.validate(token.value()))
                    .isInstanceOf(TokenValidationException.class);
        }
    }
}