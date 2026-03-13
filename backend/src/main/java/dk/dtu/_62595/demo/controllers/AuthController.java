package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.AuthResponse;
import dk.dtu._62595.demo.dto.LoginRequest;
import dk.dtu._62595.demo.dto.RegisterRequest;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/auth", consumes = { "application/xml", "application/json" })
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    public User getLoggedInUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getPrincipal() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No active session found");
		}

		Object principal = authentication.getPrincipal();
		String email;

		if (principal instanceof UserDetails userDetails) {
			email = userDetails.getUsername();
		} else {
			email = principal.toString();
		}

		return userService.find(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User record not found in database"));
	}
}
