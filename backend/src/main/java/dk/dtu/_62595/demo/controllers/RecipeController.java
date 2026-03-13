package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.CreateRecipeRequest;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.GroupRepository;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import dk.dtu._62595.demo.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import dk.dtu._62595.demo.services.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/recipes", consumes = { "application/xml", "application/json" })
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public RecipeController(
            RecipeRepository recipeRepository,
            UserRepository userRepository,
            GroupRepository groupRepository
    ) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @PostMapping
    @Transactional
    public Recipe createRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateRecipeRequest request
    ) {
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        UUID userId = UUID.fromString(jwt.getSubject());

        User owner = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Group group = null;

        if (request.getGroupId() != null) {
            group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
	@Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private GroupService groupService;
    @Autowired
	AuthController authController;

    @PostMapping
    @Transactional
    public Recipe createRecipe(@RequestBody Recipe recipeRequest) {

        User owner = authController.getLoggedInUser();

        Group group = null;

        if (recipeRequest.getGroup() != null) {
            group = groupService.getGroupById(recipeRequest.getGroup().getId());
        }

        Recipe recipe = new Recipe(
                owner,
                group,
                request.getName(),
                request.getDescription(),
                request.getInstructions(),
                request.getMealType(),
                request.getServings(),
                request.getPrepTimeMinutes(),
                request.getImageUrl(),
                request.getLastMade()
        );

        return recipeRepository.save(recipe);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Recipe recipeRequest
    ) {
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        UUID userId = UUID.fromString(jwt.getSubject());

        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

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

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        UUID userId = UUID.fromString(jwt.getSubject());

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!recipe.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        recipeRepository.delete(recipe);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public Recipe getRecipe(@PathVariable UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
}
