package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import dk.dtu._62595.demo.services.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/recipes", consumes = { "application/xml", "application/json" })
public class RecipeController {

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
}
