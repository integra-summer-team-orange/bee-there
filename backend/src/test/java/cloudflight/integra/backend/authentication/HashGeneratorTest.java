package cloudflight.integra.backend.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class HashGeneratorTest {

    @Test
    void generatePasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Password123!";
        String hash = encoder.encode(password);

        System.out.println("Plain: " + password);
        System.out.println("Hash: " + hash);

        // Verify it works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Matches: " + matches);
    }
}
