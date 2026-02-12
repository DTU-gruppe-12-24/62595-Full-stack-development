package dk.dtu._62595.demo.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Group")
public class Group {
	@Id @GeneratedValue(strategy = GenerationType.UUID)
	public final UUID id;

	public String name;

	public Group(UUID id, String name) {
		this.id = id;
		this.name = name;
	}
}