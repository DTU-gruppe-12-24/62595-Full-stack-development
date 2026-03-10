package dk.dtu._62595.demo.model;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "ingredients")
public class Ingredient {

	@Id
	@Column(columnDefinition = "CHAR(36)")
	private UUID id;

	@Column(nullable = false)
	private String name;

	private Float calories;
	private Float protein;
	private Float carbohydrates;
	private Float fat;

	@Column(name = "saturated_fat")
	private Float saturatedFat;

	private Float sugars;
	private Float salt;
	private Float price;

	public Ingredient() {}

	public Ingredient(String name, Float calories, Float price) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.calories = calories;
		this.price = price;
	}

	public UUID getId() { return id; }
	public String getName() { return name; }
	public Float getCalories() { return calories; }
	public Float getProtein() { return protein; }
	public Float getCarbohydrates() { return carbohydrates; }
	public Float getFat() { return fat; }
	public Float getSaturatedFat() { return saturatedFat; }
	public Float getSugars() { return sugars; }
	public Float getSalt() { return salt; }
	public Float getPrice() { return price; }
}
