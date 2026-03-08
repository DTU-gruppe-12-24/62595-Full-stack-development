package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile("test")
@RestController
@RequestMapping("/api/test")
public class TestUserController {

    private final UserService userService;

    public TestUserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/user/email/{email}")
    public ResponseEntity<Void> deleteTestUser(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }
}