package dk.dtu._62595.demo.services;

import dk.dtu._62595.demo.dto.AuthResponse;
import dk.dtu._62595.demo.dto.LoginRequest;
import dk.dtu._62595.demo.dto.RegisterRequest;
import dk.dtu._62595.demo.dto.UserUpdateRequest;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.UserRepository;
import dk.dtu._62595.demo.security.JwtUtil;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GroupService groupService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       GroupService groupService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.groupService = groupService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        String hash = passwordEncoder.encode(request.password());
        User user = new User(request.name(), request.email(), hash);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId().toString());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getId().toString());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public Optional<User> find(String email) {
    	return userRepository.findByEmail(email);
    }

    public Optional<User> find(UUID id) {
    	return userRepository.findById(id);
    }

    public void deleteUserByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
    }
    @Transactional
    public AuthResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Password gate
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid credentials");
        }

        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());

        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            if (request.newPassword().length() < 8) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short");
            }
            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        }

        userRepository.save(user);

        // Return a fresh token since the password/email might have changed
        return new AuthResponse(jwtUtil.generateToken(user.getId().toString()),
                user.getId(), user.getName(), user.getEmail());
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // If a user is owner of group(s), transfer ownership before deletion
        groupService.cleanupUserGroups(user);

        // Delete the user
        userRepository.deleteById(id);
    }

}
