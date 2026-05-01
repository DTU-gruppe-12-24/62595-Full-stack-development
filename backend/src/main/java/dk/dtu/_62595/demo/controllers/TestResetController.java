package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.repositories.GroupRepository;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/api/test")
public class TestResetController {

    public final GroupRepository groupRepository;
    public final RecipeRepository recipeRepository;

    public TestResetController(GroupRepository groupRepository,
                               RecipeRepository recipeRepository) {
        this.groupRepository = groupRepository;
        this.recipeRepository = recipeRepository;
    }

    @DeleteMapping("/reset")
    public ResponseEntity<Void> reset() {
        recipeRepository.deleteAll();
        groupRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
