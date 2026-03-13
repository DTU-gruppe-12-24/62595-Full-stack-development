package dk.dtu._62595.demo.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "group_table") // Group and Groups are reserved keywords in MySQL
public class Group {

	@Id
	@Column(columnDefinition = "CHAR(36)")
	private UUID id;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "group", orphanRemoval = true)
	private List<GroupMember> members;

    public Group() {}

	public Group(String name) {
		this.id = UUID.randomUUID();
		this.name = name;
	}

    public UUID getId() {   return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}