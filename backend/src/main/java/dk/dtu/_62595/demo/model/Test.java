package dk.dtu._62595.demo.model;


import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Test {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	public String name;

	public Test(Long id) {
		this.id = id;
	}

	public Long getId() { return id; }
}
