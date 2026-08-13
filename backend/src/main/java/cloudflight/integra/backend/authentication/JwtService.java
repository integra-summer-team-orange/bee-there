package cloudflight.integra.backend.authentication;

import cloudflight.integra.backend.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Provides functionality for generating and validating JWT tokens.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    /**
     * Creates a new JWT service.
     *
     * @param secret the secret key used to sign and verify JWT tokens
     * @param expirationMs the amount of time in milliseconds before a token expires
     */
    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a JWT token for the specified user.
     *
     * @param user the user for whom the token is generated
     * @return the generated JWT token
     */
    public String generateToken(User user) {
        Date now = new Date();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the email from a JWT token.
     *
     * @param token the JWT token
     * @return the email stored in the token subject
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user identifier from a JWT token.
     *
     * @param token the JWT token
     * @return the user identifier stored in the token
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Extracts the user role from a JWT token.
     *
     * @param token the JWT token
     * @return the user role stored in the token
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Checks whether a JWT token is valid.
     *
     * @param token the JWT token to validate
     * @return {@code true} if the token is valid and has not expired,
     *         {@code false} otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            extractClaim(token, Claims::getExpiration);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts a claim from a JWT token.
     *
     * @param token the JWT token
     * @param resolver the function used to extract the required claim
     * @param <T> the type of the extracted claim
     * @return the extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims =
                Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        return resolver.apply(claims);
    }
}
