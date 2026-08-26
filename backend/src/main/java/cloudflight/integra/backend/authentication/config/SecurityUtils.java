package cloudflight.integra.backend.authentication.config;

import cloudflight.integra.backend.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    /**
     * Stands in for the signed-in user while there is no login screen. Set once at startup by
     * {@code DevelopmentUserSeeder}.
     *
     * <p>RESTORE-AUTH: delete this field and its setter once login lands.
     */
    private static volatile User developmentUser;

    private SecurityUtils() {}

    /**
     * Records the user that requests without a token are attributed to.
     *
     * <p>RESTORE-AUTH: delete once login lands.
     *
     * @param user the seeded development user
     */
    public static void setDevelopmentUser(User user) {
        developmentUser = user;
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }

        // RESTORE-AUTH: without a login screen most requests arrive with no token at all, so they are
        // attributed to the seeded development user instead of failing. Restoring auth means dropping this
        // branch and letting the call fail loudly again.
        if (developmentUser != null) {
            return developmentUser;
        }

        throw new IllegalStateException("No authenticated user and no development user is available");
    }

    public static void checkOwnership(Long ownerId) {
        // RESTORE-AUTH: ownership is deliberately not enforced while the venue screens are being built
        // without a login screen. Uncomment the block below to put it back — the call sites in the services
        // were left in place on purpose so the intent stays visible.
        //
        // User currentUser = getCurrentUser();
        //
        // if (currentUser.getRole() == Role.ADMIN) {
        //     return;
        // }
        //
        // if (!currentUser.getId().equals(ownerId)) {
        //     throw new AccessDeniedException("You are not allowed to manage this resource");
        // }
    }
}
