package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.repositories.GroupRepository;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import dk.dtu._62595.demo.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public RecipeController(RecipeRepository recipeRepository,
                            UserRepository userRepository, GroupRepository groupRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @PostMapping
    @Transactional
    public Recipe createRecipe(
            @RequestParam UUID id,
            @RequestBody Recipe recipeRequest
    ) {

        User owner = userRepository.findById(recipeRequest.getOwner().getId())
                .orElseThrow(() -> new RuntimeException("Bruger ikke fundet"));

        Group group = null;

        if (recipeRequest.getGroup() != null) {
            group = groupRepository.findById(recipeRequest.getGroup().getId())
                    .orElseThrow(() -> new RuntimeException("Gruppe ikke fundet"));
        }

        Recipe recipe = new Recipe(
                owner,
                group,
                recipeRequest.getName(),
                recipeRequest.getDescription(),
                recipeRequest.getInstructions(),
                recipeRequest.getMealType(),
                recipeRequest.getServings(),
                recipeRequest.getPrepTimeMinutes(),
                recipeRequest.getImageUrl(),
                recipeRequest.getLastMade()
        );

        return recipeRepository.save(recipe);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable UUID id,
            @RequestBody Recipe recipeRequest
    ) {

        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opskrift ikke fundet"));

        existingRecipe.setName(recipeRequest.getName());
        existingRecipe.setDescription(recipeRequest.getDescription());
        existingRecipe.setInstructions(recipeRequest.getInstructions());
        existingRecipe.setMealType(recipeRequest.getMealType());
        existingRecipe.setServings(recipeRequest.getServings());
        existingRecipe.setPrepTimeMinutes(recipeRequest.getPrepTimeMinutes());
        existingRecipe.setImageUrl(recipeRequest.getImageUrl());
        existingRecipe.setLastMade(recipeRequest.getLastMade());

        return ResponseEntity.ok(recipeRepository.save(existingRecipe));
    }

    @GetMapping("/{id}")
    public Recipe getRecipe(@PathVariable UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opskrift ikke fundet"));
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Det er bare en test for mig (Lukas) :))
    @GetMapping("/test")
    public String test() {
        return "Recipe controller virker!";
    }
}
