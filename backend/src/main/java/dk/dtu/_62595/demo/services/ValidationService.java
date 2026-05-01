package dk.dtu._62595.demo.services;

import dk.dtu._62595.demo.dto.LoginRequest;
import dk.dtu._62595.demo.dto.RegisterRequest;

import java.util.regex.Pattern;

public class ValidationService {

    private ValidationService() {} // Prevent default public constructor

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static void validateRegister(RegisterRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new ValidationException("Name is required.");
        }
        validateEmail(request.email());
        validatePassword(request.password());
    }

    public static void validateLogin(LoginRequest request) {
        validateEmail(request.email());
        if (request.password() == null || request.password().isBlank()) {
            throw new ValidationException("Password is required.");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Please enter a valid email address.");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters.");
        }
    }
}

class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
