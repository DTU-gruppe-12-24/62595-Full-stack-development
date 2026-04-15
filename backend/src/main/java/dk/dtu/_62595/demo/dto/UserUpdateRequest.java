package dk.dtu._62595.demo.dto;

public record UserUpdateRequest(
        String name,
        String email,
        String currentPassword,
        String newPassword
) {}