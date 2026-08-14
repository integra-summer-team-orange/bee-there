package cloudflight.integra.backend.authentication.config;

import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static void checkOwnership(Long ownerId) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (!currentUser.getId().equals(ownerId)) {
            throw new AccessDeniedException("You are not allowed to manage this resource");
        }
    }
}
