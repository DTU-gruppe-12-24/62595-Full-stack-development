package dk.dtu._62595.demo.model;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "user_table")
public class User {

	@Id
	@Column(columnDefinition = "CHAR(36)")
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	public User() {}

	public User(String name, String email, String passwordHash) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.email = email;
		this.passwordHash = passwordHash;
	}

    public UUID getId() {   return id; }
}
