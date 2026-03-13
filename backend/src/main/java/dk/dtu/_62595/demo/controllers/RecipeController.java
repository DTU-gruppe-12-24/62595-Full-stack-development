package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.CreateRecipeRequest;
import dk.dtu._62595.demo.dto.UpdateRecipeRequest;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.GroupRepository;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final GroupRepository groupRepository;
    private final AuthController authController;

    public RecipeController(RecipeRepository recipeRepository,
                            GroupRepository groupRepository,
                            AuthController authController) {
        this.recipeRepository = recipeRepository;
        this.groupRepository = groupRepository;
        this.authController = authController;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Recipe> createRecipe(@RequestBody CreateRecipeRequest request) {
        User owner = authController.getLoggedInUser();

        Group group = null;
        if (request.groupId() != null) {
            group = groupRepository.findById(request.groupId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        }

        Recipe recipe = new Recipe(
                owner,
                group,
                request.name(),
                request.description(),
                request.instructions(),
                request.mealType(),
                request.servings(),
                request.prepTimeMinutes(),
                request.imageUrl(),
                request.lastMade()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(recipeRepository.save(recipe));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Recipe> updateRecipe(@PathVariable UUID id,
                                               @RequestBody UpdateRecipeRequest request) {
        User currentUser = authController.getLoggedInUser();

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        if (!recipe.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this recipe");
        }

        recipe.setName(request.name());
        recipe.setDescription(request.description());
        recipe.setInstructions(request.instructions());
        recipe.setMealType(request.mealType());
        recipe.setServings(request.servings());
        recipe.setPrepTimeMinutes(request.prepTimeMinutes());
        recipe.setImageUrl(request.imageUrl());
        recipe.setLastMade(request.lastMade());

        return ResponseEntity.ok(recipeRepository.save(recipe));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteRecipe(@PathVariable UUID id) {
        User currentUser = authController.getLoggedInUser();

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        if (!recipe.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this recipe");
        }

        recipeRepository.delete(recipe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        return ResponseEntity.ok(recipe);
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeRepository.findAll());
    }
}
