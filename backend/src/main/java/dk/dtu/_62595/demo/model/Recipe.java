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
@Table(name = "Recipes")
public class Recipe {
	@Id @GeneratedValue(strategy = GenerationType.UUID)
	public final UUID id;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	public User owner;

	@ManyToOne
	@JoinColumn(name = "group_id")
	public Group group;

	public String name;
	public String description;
	public String instructions;
	public String mealType;

	public Integer servings;
	public Integer prepTimeMinutes;

	public String imageUrl;

	public LocalDate lastMade;

	public Recipe(UUID id, User owner, Group group, String name, String description, String instructions, String mealType, Integer servings, Integer prepTimeMinutes, String imageUrl, LocalDate lastMade) {
		this.id = id;
		this.owner = owner;
		this.group = group;
		this.name = name;
		this.description = description;
		this.instructions = instructions;
		this.mealType = mealType;
		this.servings = servings;
		this.prepTimeMinutes = prepTimeMinutes;
		this.imageUrl = imageUrl;
		this.lastMade = lastMade;
	}
}