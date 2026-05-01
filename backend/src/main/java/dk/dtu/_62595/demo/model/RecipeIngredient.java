package dk.dtu._62595.demo.model;

import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

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

	@Column(columnDefinition = "VARCHAR(255)")
	@Enumerated(EnumType.STRING)
	private Unit unit;

	public RecipeIngredient() {}

	public RecipeIngredient(Recipe recipe, Ingredient ingredient, Float amount, Unit unit) {
		this.id = new RecipeIngredientId(recipe.getId(), ingredient.getId());
		this.recipe = recipe;
		this.ingredient = ingredient;
		this.amount = amount;
		this.unit = unit;
	}

	public RecipeIngredientId getId() {
		return id;
	}
	public Recipe getRecipe() { return recipe; }
	public Ingredient getIngredient() { return ingredient; }
	public Float getAmount() { return amount; }
	public Unit getUnit() { return unit; }

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

	public enum Unit {
		GRAM("g", 1.0f),
		KILOGRAM("kg", 1000.0f),
		MILLILITER("mL", 1.0f),
		DECILITER("dL", 100.0f),
		LITER("L", 1000.0f),
		TABLESPOON("tbsp", 15.0f),
		TEASPOON("tsp", 5.0f),
		PINCH("pinch", 0.36f),
		POUND("lb", 453.6f),
		OUNCE("oz", 28.35f),
		CUP("cup", 240.0f),
		TIN("tin", 400.0f),
		PIECE("piece", 50.0f),
		NOTHING("", 0.0f);

		private final String string;
		private final float gramsPerUnit;
		private Unit(String string, float gramsPerUnit) {
			this.string = string;
			this.gramsPerUnit = gramsPerUnit;
		}

		public float getGramsPerUnit() {
			return gramsPerUnit;
		}

		@Override
		public String toString() {
			return string;
		}

		public static Unit fromString(String string) {
			String check = string.toLowerCase().trim();
			for (Unit unit : Unit.values()) {
				if (unit.string.equalsIgnoreCase(check))
					return unit;
			}

			if (Pattern.matches("grams?", check)) return GRAM;
			if (Pattern.matches("kilograms?", check)) return KILOGRAM;
			if (Pattern.matches("milliliters?", check)) return MILLILITER;
			if (Pattern.matches("deciliters?", check)) return DECILITER;
			if (Pattern.matches("liters?", check)) return LITER;
			if (Pattern.matches("tbl?s(pn?)?|tablespoons?", check)) return TABLESPOON;
			if (Pattern.matches("ts(pn?)?|teaspoons?", check)) return TEASPOON;
			if (Pattern.matches("pinch(es)?", check)) return PINCH;
			if (Pattern.matches("ounce(s)?", check)) return OUNCE;
			if (Pattern.matches("cups?", check)) return CUP;
			if (Pattern.matches("tins?", check)) return TIN;

			return NOTHING;
		}
    }
}