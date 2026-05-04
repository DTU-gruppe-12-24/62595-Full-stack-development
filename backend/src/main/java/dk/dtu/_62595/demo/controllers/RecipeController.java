package dk.dtu._62595.demo.controllers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dk.dtu._62595.demo.dto.CreateRecipeRequest;
import dk.dtu._62595.demo.dto.ExternalRecipeDto;
import dk.dtu._62595.demo.dto.RecipeDto;
import dk.dtu._62595.demo.dto.RecipeIngredientDto;
import dk.dtu._62595.demo.dto.RecipeIngredientRequest;
import dk.dtu._62595.demo.dto.UpdateRecipeRequest;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.Ingredient;
import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.model.RecipeIngredient;
import dk.dtu._62595.demo.model.RecipeIngredient.Unit;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.IngredientRepository;
import dk.dtu._62595.demo.repositories.RecipeIngredientRepository;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import dk.dtu._62595.demo.services.ExternalRecipeService;
import dk.dtu._62595.demo.services.GroupService;

@RestController
@RequestMapping(value = "/api/recipes", produces="application/json")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    @Autowired
    private GroupService groupService;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private AuthController authController;
    @Autowired
    private ExternalRecipeService externalRecipeService;

    @PostMapping
    @Transactional
    public ResponseEntity<RecipeDto> createRecipe(@RequestBody CreateRecipeRequest request) {
        User owner = authController.getLoggedInUser();

        Group group = null;
        if (request.groupId() != null)
            group = groupService.getGroupById(request.groupId());

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
        if (!canUserAccessRecipe(recipe, authController.getLoggedInUser())) throw new AuthorizationDeniedException("You do not have permission to access this recipe!");

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

        if (!recipe.getOwner().getId().equals(currentUser.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this recipe");

        recipeIngredientRepository.findByRecipe(recipe)
                .forEach(recipeIngredientRepository::delete);
        recipeRepository.delete(recipe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public RecipeDto getRecipe(@PathVariable UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        if (!canUserAccessRecipe(recipe, authController.getLoggedInUser())) throw new AuthorizationDeniedException("You do not have permission to access this recipe!");

        return toDto(recipe, recipeIngredientRepository.findByRecipe(recipe));
    }

    @GetMapping
    public List<RecipeDto> getAllRecipes() {
    	User user = authController.getLoggedInUser();
    	List<Group> groups = groupService.getGroupsForUser(user);

     	// Get all recipes a user has access to (either through ownership or through a group)
        List<Recipe> recipes = recipeRepository.findByOwner(user);
        recipes.addAll(
	        groups.stream().map(group -> recipeRepository.findByGroup(group)).flatMap(Collection::stream).toList()
        );

        // Convert to dto
        return recipes.stream().distinct().map(r -> toDto(r, recipeIngredientRepository.findByRecipe(r))).toList();
    }

   	@PutMapping("/{recipeId}/group")
	public void addToGroup(@PathVariable UUID recipeId, @RequestBody(required = false) UUID groupId) {
		User owner = authController.getLoggedInUser();

		Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
		if (!recipe.getOwner().equals(owner)) throw new AuthorizationDeniedException("You do not have permission to edit this recipe!");

		System.out.println(groupId);
		Group group = null;
		if (groupId != null) {
			group = groupService.getGroupById(groupId);
			if (!groupService.canUserViewGroup(group, owner)) throw new AuthorizationDeniedException("You do not have permission to access this group!");
		}

		recipe.setGroup(group);
		recipeRepository.save(recipe);
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
                            new RecipeIngredient(recipe, ingredient, r.amount(), Unit.fromString(r.unit()))
                    );
                })
                .toList();
    }

    @GetMapping("/external/search")
    public List<ExternalRecipeDto> externalSearch(@RequestParam String q) {
        return externalRecipeService.search(q);
    }

    private RecipeDto toDto(Recipe recipe, List<RecipeIngredient> ingredients) {
        List<RecipeIngredientDto> ingredientDtos = ingredients.stream()
                .map(ri -> new RecipeIngredientDto(
                        ri.getIngredient().getId(),
                        ri.getIngredient().getName(),
                        ri.getAmount(),
                        ri.getUnit().toString()
                ))
                .toList();

        return new RecipeDto(
                recipe.getId(),
                recipe.getOwner().getName(),
                recipe.getOwner().getId(),
                recipe.getGroup() != null ? recipe.getGroup().getId() : null,
                recipe.getGroup() != null ? recipe.getGroup().getName() : null,
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

	private boolean canUserAccessRecipe(Recipe recipe, User user) {
		Group group = recipe.getGroup();
		if (group != null)
			if (!groupService.canUserViewGroup(group, user)) return false;

		return recipe.getOwner().equals(user);
	}
}
