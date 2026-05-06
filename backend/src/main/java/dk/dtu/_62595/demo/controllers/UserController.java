package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.AuthResponse;
import dk.dtu._62595.demo.dto.UserUpdateRequest;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users", produces="application/json")
public class UserController {

    @Autowired
    AuthController authController;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe() {
        User user = authController.getLoggedInUser();
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateMe(@RequestBody UserUpdateRequest request) {
        User user = authController.getLoggedInUser();
        AuthResponse updated = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        return ResponseEntity.ok(authController.getLoggedInUser());
    }
}