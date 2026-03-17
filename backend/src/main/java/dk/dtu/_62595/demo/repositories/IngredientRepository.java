package dk.dtu._62595.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Ingredient;

public interface IngredientRepository extends Repository<Ingredient, UUID> {

    // Persistence operations
    Ingredient save(Ingredient ingredient);
    void deleteById(UUID id);

    // Retrieval operations
    Optional<Ingredient> findById(UUID id);
    List<Ingredient> findAll();

    // Derived queries
    Optional<Ingredient> findByName(String name);
    @org.springframework.data.jpa.repository.Query(
        """
        SELECT i FROM Ingredient i
        WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY
            CASE
                WHEN LOWER(i.name) = LOWER(:query)                    THEN 0
                WHEN LOWER(i.name) LIKE LOWER(CONCAT(:query, '%'))    THEN 1
                ELSE                                                       2
            END,
            i.name
        LIMIT 20
        """
    )
    List<Ingredient> searchByRelevance(String query);
}
