package dk.dtu._62595.demo.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private Float calories;

    @Column
    private Float protein;

    @Column
    private Float carbohydrates;

    @Column
    private Float fat;

    @Column(name = "saturated_fat")
    private Float saturatedFat;

    @Column
    private Float sugars;

    @Column
    private Float salt;

    @Column
    private Float price;

    public Ingredient() {}

    public Ingredient(String name, Float calories, Float protein, Float carbohydrates,
                      Float fat, Float saturatedFat, Float sugars, Float salt, Float price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.saturatedFat = saturatedFat;
        this.sugars = sugars;
        this.salt = salt;
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

    public void setName(String name) { this.name = name; }
    public void setCalories(Float calories) { this.calories = calories; }
    public void setProtein(Float protein) { this.protein = protein; }
    public void setCarbohydrates(Float carbohydrates) { this.carbohydrates = carbohydrates; }
    public void setFat(Float fat) { this.fat = fat; }
    public void setSaturatedFat(Float saturatedFat) { this.saturatedFat = saturatedFat; }
    public void setSugars(Float sugars) { this.sugars = sugars; }
    public void setSalt(Float salt) { this.salt = salt; }
    public void setPrice(Float price) { this.price = price; }
}
