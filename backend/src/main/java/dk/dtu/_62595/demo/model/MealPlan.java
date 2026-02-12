package dk.dtu._62595.demo.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "Meal_Plans")
public class MealPlan {
	@Id @GeneratedValue(strategy = GenerationType.UUID)
	public final UUID id;

	@ManyToOne
	@JoinColumn(name = "group_id", nullable = false)
	public Group group;

	@ManyToOne
	@JoinColumn(name = "recipe_id", nullable = false)
	public Recipe recipe;
	public LocalDate scheduledDate;
	public String mealSlot;

	public MealPlan(UUID id, Group group, Recipe recipe, LocalDate scheduledDate, String mealSlot) {
		this.id = id;
		this.group = group;
		this.recipe = recipe;
		this.scheduledDate = scheduledDate;
		this.mealSlot = mealSlot;
	}
}