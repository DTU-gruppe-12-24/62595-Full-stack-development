package dk.dtu._62595.demo.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "shopping_lists")
public class ShoppingList {

	@Id
	@Column(columnDefinition = "CHAR(36)")
	private UUID id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "group_id", columnDefinition = "CHAR(36)", nullable = false)
	private Group group;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ingredient_id", columnDefinition = "CHAR(36)", nullable = false)
	private Ingredient ingredient;

	@Column(nullable = false)
	private float amount;

	@Column(name = "is_bought", nullable = false)
	private boolean isBought;

	public ShoppingList() {}

	public ShoppingList(Group group, Ingredient ingredient, float amount, boolean isBought) {
		this.id = UUID.randomUUID();
		this.group = group;
		this.ingredient = ingredient;
		this.amount = amount;
		this.isBought = isBought;
	}

	public UUID getId() {
		return id;
	}
	public Group getGroup() { return group; }
	public Ingredient getIngredient() { return ingredient; }
	public float getAmount() { return amount; }
	public boolean isBought() {return isBought; }
}