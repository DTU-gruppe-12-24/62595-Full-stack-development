package dk.dtu._62595.demo.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GroupTable") // Group and Groups are reserved keywords in MySQL
public class Group {
	@Id @GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String name;

    public Group() {}

	public Group(String name) {
		this.name = name;
	}

    public UUID getId() {   return id; }

    public String getName() { return name; }
}