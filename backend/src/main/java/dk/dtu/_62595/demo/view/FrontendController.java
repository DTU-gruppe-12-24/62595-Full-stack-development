package dk.dtu._62595.demo.view;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller("/")
public class FrontendController {
	// Home page should just return the vue frontend
	@GetMapping("/")
	public @ResponseBody byte[] getHome() throws IOException {
		InputStream stream = getClass().getResourceAsStream("/static/index.html");
		return IOUtils.toByteArray(stream);
	}

	// Any unknown api route will be 404
	@RequestMapping("/api/{path:[^\\.]*}")
	public @ResponseBody byte[] getAnyApi() {
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found");
	}

	// Any unknown route (different vue pages) will be handled by Vue router
	@GetMapping("/**/{path:[^\\.]*}")
	public @ResponseBody byte[] getAny() throws IOException {
		InputStream stream = getClass().getResourceAsStream("/static/index.html");
		return IOUtils.toByteArray(stream);
	}
}
