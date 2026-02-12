package dk.dtu._62595.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "Shopping_Lists")
public class ShoppingList {
	@Id @GeneratedValue(strategy = GenerationType.UUID)
	public final UUID id;

	@ManyToOne
	@JoinColumn(name = "group_id")
	public Group group;
	@ManyToOne
	@JoinColumn(name = "ingredient_id")
	public Ingredient ingredient;
	public float amount;
	public boolean is_bought;

	public ShoppingList(UUID id, Group group, Ingredient ingredient, float amount, boolean is_bought) {
		this.id = id;
		this.group = group;
		this.ingredient = ingredient;
		this.amount = amount;
		this.is_bought = is_bought;
	}
}