package dk.dtu._62595.demo.model;

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
@Table(name = "Recipe_Ingredients")
public class RecipeIngredient {

	@EmbeddedId
	public RecipeIngredientId id;

	@ManyToOne
	@MapsId("recipeId")
	@JoinColumn(name = "recipe_id", nullable = false)
	public Recipe recipe;

	@ManyToOne
	@MapsId("ingredientId")
	@JoinColumn(name = "ingredient_id", nullable = false)
	public Ingredient ingredient;

	@Column(name = "amount")
	public Float amount;

	@Column(name = "unit")
	public String unit;

	public RecipeIngredient(RecipeIngredientId id) {
		this.id = id;
	}

	@Embeddable
	public static class RecipeIngredientId {
		@Column(name = "recipe_id")
		        public UUID recipeId;

		@Column(name = "ingredient_id")
		        public UUID ingredientId;

		        public RecipeIngredientId(UUID recipeId, UUID ingredientId) {
			this.recipeId = recipeId;
			this.ingredientId = ingredientId;
		}

		public RecipeIngredientId() {}

		public RecipeIngredientId(Recipe recipe, Ingredient ingredient) {
			this.recipeId = recipe.id;
			this.ingredientId = ingredient.id;
		}
	}

	public RecipeIngredient() {}

	public RecipeIngredient(RecipeIngredientId id, Recipe recipe, Ingredient ingredient, Float amount, String unit) {
		this.id = id;
		this.recipe = recipe;
		this.ingredient = ingredient;
		this.amount = amount;
		this.unit = unit;
	}
}