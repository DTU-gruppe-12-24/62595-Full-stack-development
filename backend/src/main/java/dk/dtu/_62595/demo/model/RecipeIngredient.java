package dk.dtu._62595.demo.model;

import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

	@EmbeddedId
	private RecipeIngredientId id;

	@ManyToOne(optional = false)
	@MapsId("recipeId")
	@JoinColumn(name = "recipe_id", columnDefinition = "CHAR(36)", nullable = false)
	private Recipe recipe;

	@ManyToOne(optional = false)
	@MapsId("ingredientId")
	@JoinColumn(name = "ingredient_id", columnDefinition = "CHAR(36)", nullable = false)
	private Ingredient ingredient;

	private Float amount;

	private String unit;

	public RecipeIngredient() {}

	public RecipeIngredient(Recipe recipe, Ingredient ingredient, Float amount, String unit) {
		this.id = new RecipeIngredientId(recipe.getId(), ingredient.getId());
		this.recipe = recipe;
		this.ingredient = ingredient;
		this.amount = amount;
		this.unit = unit;
	}

	public RecipeIngredientId getId() {
		return id;
	}

	@Embeddable
	public static class RecipeIngredientId implements Serializable {

		@Column(name = "recipe_id", columnDefinition = "CHAR(36)")
		        private UUID recipeId;

		@Column(name = "ingredient_id", columnDefinition = "CHAR(36)")
		        private UUID ingredientId;

		public RecipeIngredientId() {}

		public RecipeIngredientId(UUID recipeId, UUID ingredientId) {
			this.recipeId = recipeId;
			this.ingredientId = ingredientId;
		}

		public UUID getIngredientId() {
			return ingredientId;
		}

		public UUID getRecipeId() {
			return recipeId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof RecipeIngredientId)) return false;
			RecipeIngredientId that = (RecipeIngredientId) o;
			return recipeId.equals(that.recipeId) &&
					ingredientId.equals(that.ingredientId);
		}

		@Override
		public int hashCode() {
			return recipeId.hashCode() + ingredientId.hashCode();
		}
	}
}