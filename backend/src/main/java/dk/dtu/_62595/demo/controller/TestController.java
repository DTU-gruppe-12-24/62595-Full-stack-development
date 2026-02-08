package dk.dtu._62595.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/test")
public class TestController {
	@GetMapping
	public String test() {
		return "Test";
	}
}
