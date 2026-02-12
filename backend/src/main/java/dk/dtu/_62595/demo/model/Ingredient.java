package dk.dtu._62595.demo.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Ingredients")
public class Ingredient {
	@Id @GeneratedValue(strategy = GenerationType.UUID)
	public final UUID id;

	public String name;
	public Float calories;
	public Float price;

	public Ingredient(UUID id, String name, Float calories, Float price) {
		this.id = id;
		this.name = name;
		this.calories = calories;
		this.price = price;
	}
}