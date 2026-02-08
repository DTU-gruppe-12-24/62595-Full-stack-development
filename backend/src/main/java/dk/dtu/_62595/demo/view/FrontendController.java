package dk.dtu._62595.demo.view;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("/")
public class FrontendController {
	// Home page should just return the vue frontend
	@GetMapping("/")
	public @ResponseBody byte[] getHome() throws IOException {
		InputStream stream = getClass().getResourceAsStream("/static/index.html");
		return IOUtils.toByteArray(stream);
	}

	// Any unknown route (different vue pages) will be handled by Vue router
	@GetMapping("/{route:^(?!api\\/$).+$}")
	public @ResponseBody byte[] getAny() throws IOException {
		InputStream stream = getClass().getResourceAsStream("/static/index.html");
		return IOUtils.toByteArray(stream);
	}
}
