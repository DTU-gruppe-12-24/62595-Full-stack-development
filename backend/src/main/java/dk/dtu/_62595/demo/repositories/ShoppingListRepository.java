package dk.dtu._62595.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.Ingredient;
import dk.dtu._62595.demo.model.ShoppingList;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, UUID> {
	// Persistence operations
	ShoppingList save(ShoppingList entity);
	void deleteById(UUID id);

	// Retrieval operations
	Optional<ShoppingList> findById(UUID id);
	List<ShoppingList> findAll();

	// Derived queries
	List<ShoppingList> findByGroup(Group group);
	List<ShoppingList> findByGroupAndIngredient(Group group, Ingredient ingredient);
	List<ShoppingList> findByGroupAndIsBought(Group group, boolean isBought);
}