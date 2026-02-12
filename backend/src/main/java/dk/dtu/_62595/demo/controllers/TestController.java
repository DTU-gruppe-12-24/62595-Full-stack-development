package dk.dtu._62595.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.dtu._62595.demo.repositories.TestRepository;

@RestController()
@RequestMapping("/api/test")
public class TestController {
	@Autowired
	TestRepository testRepository;

	@GetMapping
	public String test() {
		testRepository.findByName("test");
		return "Test";
	}
}
