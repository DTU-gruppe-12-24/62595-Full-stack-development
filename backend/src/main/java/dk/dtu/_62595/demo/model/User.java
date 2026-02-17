package dk.dtu._62595.demo.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_table")
public class User {
	@Id @GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	public String name;
	public String email;
	public String passwordHash;

	public User() {}

	public User(String name, String email, String passwordHash) {
		this.name = name;
		this.email = email;
		this.passwordHash = passwordHash;
	}

    public UUID getId() {   return id; }
}
