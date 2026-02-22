package dk.dtu._62595.demo.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "recipes")
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
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

	public void setId(UUID id) {
		this.id = id;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getMealType() {
		return mealType;
	}

	public void setMealType(String mealType) {
		this.mealType = mealType;
	}

	public Integer getServings() {
		return servings;
	}

	public void setServings(Integer servings) {
		this.servings = servings;
	}

	public Integer getPrepTimeMinutes() {
		return prepTimeMinutes;
	}

	public void setPrepTimeMinutes(Integer prepTimeMinutes) {
		this.prepTimeMinutes = prepTimeMinutes;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public LocalDate getLastMade() {
		return lastMade;
	}

	public void setLastMade(LocalDate lastMade) {
		this.lastMade = lastMade;
	}
}
