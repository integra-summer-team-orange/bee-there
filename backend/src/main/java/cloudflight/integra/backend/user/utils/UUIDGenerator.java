package cloudflight.integra.backend.user.utils;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * Generates unique UUIDs that do not conflict with existing identifiers.
 */
@Component
public class UUIDGenerator {

    /**
     * Generates a new UUID that is not already present in the provided set.
     *
     * @param existingIds the set of UUIDs that are already in use
     * @return a unique UUID not contained in the provided set
     */
    public UUID next(Set<UUID> existingIds) {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (existingIds.contains(id));

        return id;
    }
}

