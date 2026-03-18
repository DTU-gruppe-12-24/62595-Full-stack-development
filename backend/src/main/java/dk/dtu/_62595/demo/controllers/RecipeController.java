package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.*;
import dk.dtu._62595.demo.model.*;
import dk.dtu._62595.demo.repositories.GroupRepository;
import dk.dtu._62595.demo.repositories.IngredientRepository;
import dk.dtu._62595.demo.repositories.RecipeIngredientRepository;
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
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final GroupRepository groupRepository;
    private final IngredientRepository ingredientRepository;
    private final AuthController authController;

    public RecipeController(RecipeRepository recipeRepository,
                            RecipeIngredientRepository recipeIngredientRepository,
                            GroupRepository groupRepository,
                            IngredientRepository ingredientRepository,
                            AuthController authController) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.groupRepository = groupRepository;
        this.ingredientRepository = ingredientRepository;
        this.authController = authController;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<RecipeDto> createRecipe(@RequestBody CreateRecipeRequest request) {
        User owner = authController.getLoggedInUser();

        Group group = null;
        if (request.groupId() != null) {
            group = groupRepository.findById(request.groupId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        }

        Recipe recipe = new Recipe(
                owner, group, request.name(), request.description(), request.instructions(),
                request.mealType(), request.servings(), request.prepTimeMinutes(),
                request.imageUrl(), request.lastMade()
        );
        recipeRepository.save(recipe);

        List<RecipeIngredient> ingredients = saveIngredients(recipe, request.ingredients());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(recipe, ingredients));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<RecipeDto> updateRecipe(@PathVariable UUID id,
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
        recipeRepository.save(recipe);

        // Replace all ingredients
        recipeIngredientRepository.findByRecipe(recipe)
                .forEach(recipeIngredientRepository::delete);
        List<RecipeIngredient> ingredients = saveIngredients(recipe, request.ingredients());

        return ResponseEntity.ok(toDto(recipe, ingredients));
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

        recipeIngredientRepository.findByRecipe(recipe)
                .forEach(recipeIngredientRepository::delete);
        recipeRepository.delete(recipe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipe(@PathVariable UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        return ResponseEntity.ok(toDto(recipe, recipeIngredientRepository.findByRecipe(recipe)));
    }

    @GetMapping
    public ResponseEntity<List<RecipeDto>> getAllRecipes() {
        List<RecipeDto> recipes = recipeRepository.findAll().stream()
                .map(r -> toDto(r, recipeIngredientRepository.findByRecipe(r)))
                .toList();
        return ResponseEntity.ok(recipes);
    }

    private List<RecipeIngredient> saveIngredients(Recipe recipe, List<RecipeIngredientRequest> requests) {
        if (requests == null || requests.isEmpty()) return List.of();
        return requests.stream()
                .filter(r -> r.ingredientName() != null && !r.ingredientName().isBlank())
                .map(r -> {
                    Ingredient ingredient = ingredientRepository
                            .findByName(r.ingredientName().trim())
                            .orElseGet(() -> ingredientRepository.save(
                                    new Ingredient(r.ingredientName().trim(), null, null, null, null, null, null, null, null)
                            ));
                    return recipeIngredientRepository.save(
                            new RecipeIngredient(recipe, ingredient, r.amount(), r.unit())
                    );
                })
                .toList();
    }

    private RecipeDto toDto(Recipe recipe, List<RecipeIngredient> ingredients) {
        List<RecipeIngredientDto> ingredientDtos = ingredients.stream()
                .map(ri -> new RecipeIngredientDto(
                        ri.getIngredient().getId(),
                        ri.getIngredient().getName(),
                        ri.getAmount(),
                        ri.getUnit()
                ))
                .toList();

        return new RecipeDto(
                recipe.getId(),
                recipe.getOwner().getName(),
                recipe.getOwner().getId(),
                recipe.getGroup() != null ? recipe.getGroup().getId() : null,
                recipe.getName(),
                recipe.getDescription(),
                recipe.getInstructions(),
                recipe.getMealType(),
                recipe.getServings(),
                recipe.getPrepTimeMinutes(),
                recipe.getImageUrl(),
                recipe.getLastMade(),
                ingredientDtos
        );
    }
}
