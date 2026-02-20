package dk.dtu._62595.demo.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_plans")
public class MealPlan {
	@Id
	@Column(columnDefinition = "CHAR(36)")
	private UUID id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "group_id", columnDefinition = "CHAR(36)", nullable = false)
	private Group group;

	@ManyToOne
	@JoinColumn(name = "recipe_id", columnDefinition = "CHAR(36)", nullable = false)
	private Recipe recipe;

	@Column(name = "scheduled_date", nullable = false)
	private LocalDate scheduledDate;

	@Column(name = "meal_slot", nullable = false)
	private String mealSlot;

	public MealPlan() {}

	public MealPlan(Group group, Recipe recipe, LocalDate scheduledDate, String mealSlot) {
		this.id = UUID.randomUUID();
		this.group = group;
		this.recipe = recipe;
		this.scheduledDate = scheduledDate;
		this.mealSlot = mealSlot;
	}

	public UUID getId() {
		return id;
	}
}