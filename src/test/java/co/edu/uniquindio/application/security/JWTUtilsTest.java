package co.edu.uniquindio.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilsTest {

    private final JWTUtils jwtUtils = new JWTUtils();

    @Test
    @DisplayName("Debe generar token con subject y claims esperados")
    void generateTokenWithSubjectAndClaims() {
        String token = jwtUtils.generateToken("user-1", Map.of(
                "email", "user@test.com",
                "name", "User Test",
                "role", "ROLE_USER"
        ));

        Jws<Claims> parsed = jwtUtils.parseJwt(token);

        assertEquals("user-1", parsed.getPayload().getSubject());
        assertEquals("user@test.com", parsed.getPayload().get("email"));
        assertEquals("User Test", parsed.getPayload().get("name"));
        assertEquals("ROLE_USER", parsed.getPayload().get("role"));
        assertNotNull(parsed.getPayload().getIssuedAt());
        assertNotNull(parsed.getPayload().getExpiration());
        assertTrue(parsed.getPayload().getExpiration().after(parsed.getPayload().getIssuedAt()));
    }

    @Test
    @DisplayName("Debe rechazar token mal formado")
    void parseRejectsMalformedToken() {
        assertThrows(JwtException.class, () -> jwtUtils.parseJwt("not-a-valid-token"));
    }

    @Test
    @DisplayName("Debe rechazar token nulo")
    void parseRejectsNullToken() {
        assertThrows(IllegalArgumentException.class, () -> jwtUtils.parseJwt(null));
    }
}
