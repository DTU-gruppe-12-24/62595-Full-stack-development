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
	public String meal_type;

	public Integer servings;
	public Integer prep_time_minutes;

	public String image_url;

	public LocalDate last_made;

	public Recipe(UUID id, User owner, Group group, String name, String description, String instructions, String meal_type, Integer servings, Integer prep_time_minutes, String image_url, LocalDate last_made) {
		this.id = id;
		this.owner = owner;
		this.group = group;
		this.name = name;
		this.description = description;
		this.instructions = instructions;
		this.meal_type = meal_type;
		this.servings = servings;
		this.prep_time_minutes = prep_time_minutes;
		this.image_url = image_url;
		this.last_made = last_made;
	}
}