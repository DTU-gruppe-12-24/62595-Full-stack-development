package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.CreateIngredientRequest;
import dk.dtu._62595.demo.dto.IngredientDto;
import dk.dtu._62595.demo.model.Ingredient;
import dk.dtu._62595.demo.repositories.IngredientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;

    public IngredientController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(@RequestBody CreateIngredientRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Ingredient name must not be empty");
        }

        String name = request.name().trim();
        Ingredient incoming = toIngredient(name, request);

        return ingredientRepository.findByName(name)
                .map(existing -> {
                    if (countNonNull(incoming) > countNonNull(existing)) {
                        // Preserve the existing id and replace all fields
                        copyFields(incoming, existing);
                        Ingredient updated = ingredientRepository.save(existing);
                        return ResponseEntity.ok(toDto(updated));
                    }
                    return ResponseEntity.ok(toDto(existing));
                })
                .orElseGet(() -> {
                    Ingredient saved = ingredientRepository.save(incoming);
                    return ResponseEntity.status(201).body(toDto(saved));
                });
    }

    private Ingredient toIngredient(String name, CreateIngredientRequest r) {
        return new Ingredient(name, r.calories(), r.protein(), r.carbohydrates(),
                r.fat(), r.saturatedFat(), r.sugars(), r.salt(), r.price());
    }

    private int countNonNull(Ingredient i) {
        int count = 0;
        if (i.getCalories()      != null) count++;
        if (i.getProtein()       != null) count++;
        if (i.getCarbohydrates() != null) count++;
        if (i.getFat()           != null) count++;
        if (i.getSaturatedFat()  != null) count++;
        if (i.getSugars()        != null) count++;
        if (i.getSalt()          != null) count++;
        if (i.getPrice()         != null) count++;
        return count;
    }

    private void copyFields(Ingredient from, Ingredient to) {
        to.setCalories(from.getCalories());
        to.setProtein(from.getProtein());
        to.setCarbohydrates(from.getCarbohydrates());
        to.setFat(from.getFat());
        to.setSaturatedFat(from.getSaturatedFat());
        to.setSugars(from.getSugars());
        to.setSalt(from.getSalt());
        to.setPrice(from.getPrice());
    }

    private IngredientDto toDto(Ingredient i) {
        return new IngredientDto(i.getId(), i.getName());
    }
}
