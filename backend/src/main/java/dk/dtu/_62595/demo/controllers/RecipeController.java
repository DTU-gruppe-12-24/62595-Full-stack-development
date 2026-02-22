package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.model.Recipe;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.RecipeRepository;
import dk.dtu._62595.demo.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeController(RecipeRepository recipeRepository,
                            UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Recipe createRecipe(
            @RequestParam UUID ownerId,
            @RequestBody Recipe recipeRequest
    ) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Bruger ikke fundet :(")); // Lidt en brøler :)))

        Recipe recipe = new Recipe(
                recipeRequest.getOwner(),
                recipeRequest.getGroup(),
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

    @PutMapping
    public Recipe updateRecipe(
            @RequestParam UUID id,
            @RequestBody Recipe updatedData
    ) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new RuntimeException("Opskrift ikke fundet..."));

        recipe.setName(updatedData.getName());
        recipe.setDescription(updatedData.getDescription());
        recipe.setInstructions(updatedData.getInstructions());
        recipe.setMealType(updatedData.getMealType());
        recipe.setServings(updatedData.getServings());
        recipe.setPrepTimeMinutes(updatedData.getPrepTimeMinutes());
        recipe.setImageUrl(updatedData.getImageUrl());
        recipe.setLastMade(updatedData.getLastMade());

        return recipeRepository.save(recipe);
    }
}
