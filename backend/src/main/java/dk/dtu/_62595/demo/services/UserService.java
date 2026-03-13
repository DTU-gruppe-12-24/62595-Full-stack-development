package dk.dtu._62595.demo.services;

import dk.dtu._62595.demo.dto.AuthResponse;
import dk.dtu._62595.demo.dto.LoginRequest;
import dk.dtu._62595.demo.dto.RegisterRequest;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.UserRepository;
import dk.dtu._62595.demo.security.JwtUtil;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        String hash = passwordEncoder.encode(request.password());
        User user = new User(request.name(), request.email(), hash);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public Optional<User> find(String email) {
    	return userRepository.findByEmail(email);
    }

    public Optional<User> find(UUID id) {
    	return userRepository.findById(id);
    }
}
