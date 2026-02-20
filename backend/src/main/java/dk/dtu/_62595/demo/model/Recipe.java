package dk.dtu._62595.demo.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "recipes")
public class Recipe {

	@Id
	@Column(columnDefinition = "CHAR(36)")
	private UUID id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "owner_id", columnDefinition = "CHAR(36)", nullable = false)
	private User owner;

	@ManyToOne
	@JoinColumn(name = "group_id", columnDefinition = "CHAR(36)")
	private Group group;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(columnDefinition = "TEXT")
	private String instructions;

	@Column(name = "meal_type")
	private String mealType;

	private Integer servings;

	@Column(name = "prep_time_minutes")
	private Integer prepTimeMinutes;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "last_made")
	private LocalDate lastMade;

	public Recipe() {}

	public Recipe(User owner, Group group, String name, String description, String instructions, String mealType, Integer servings, Integer prepTimeMinutes, String imageUrl, LocalDate lastMade) {
		this.id = UUID.randomUUID();
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

	public UUID getId() {
		return id;
	}
}