package cloudflight.integra.backend.user.model;

import cloudflight.integra.backend.user.validation.StrongPassword;

public record PasswordRequestDto(@StrongPassword String password) {}
