package cloudflight.integra.backend.authentication.config;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()";

    private final SecureRandom random = new SecureRandom();

    public String generate(int length) {

        List<Character> password = new ArrayList<>();

        password.add(randomCharacter(UPPERCASE));
        password.add(randomCharacter(DIGITS));
        password.add(randomCharacter(SYMBOLS));

        String allCharacters = UPPERCASE + LOWERCASE + DIGITS + SYMBOLS;

        while (password.size() < length) {
            password.add(randomCharacter(allCharacters));
        }

        Collections.shuffle(password, random);

        StringBuilder result = new StringBuilder(length);

        for (char character : password) {
            result.append(character);
        }

        return result.toString();
    }

    private char randomCharacter(String characters) {
        return characters.charAt(random.nextInt(characters.length()));
    }
}
