package dk.dtu._62595.demo.services;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dk.dtu._62595.demo.dto.ExternalRecipeDto;
import dk.dtu._62595.demo.dto.RecipeIngredientDto;
import dk.dtu._62595.demo.repositories.IngredientRepository;

@Service
public class ExternalRecipeService {
	private final String baseUrl = "https://www.themealdb.com/api/json/v1/1/";

	@Autowired
	private IngredientRepository ingredientRepository;

	private Float toNumber(String str) {
		try {
			if (str.contains("/")) {
				return Float.parseFloat(str.split("/")[0]) / Float.parseFloat(str.split("/")[1]);
			}
			return Float.parseFloat(str);
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public List<ExternalRecipeDto> search(String searchTerm) {
		try {
			URL url = URI.create(baseUrl + "search.php?s=" + URLEncoder.encode(searchTerm, "UTF-8") ).toURL();
			System.out.println(url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);

			// Complete request
			con.connect();
			int status = con.getResponseCode();
			if (status < 200 || status > 299) throw new Exception("HTTP error: " + status);
			// Parse response
			JSONObject object = (JSONObject) new JSONParser().parse(new InputStreamReader(con.getInputStream()));
			JSONArray array = (JSONArray) object.get("meals");

			var recipes = new ArrayList<ExternalRecipeDto>();
			for (Object obj : array) {
				JSONObject recipe = (JSONObject) obj;

				var ingredients = new ArrayList<RecipeIngredientDto>();
				for (int i = 1; i <= 20; i++) {
					try {
						if (recipe.get("strMeasure" + i).toString().isBlank()) continue;
						String name = recipe.get("strIngredient" + i) != null ? recipe.get("strIngredient" + i).toString() : null;
						String strMeasure = recipe.get("strMeasure" + i).toString();
						List<String> measure = new ArrayList<>(0); //List.of(strMeasure.split(" "));

						Matcher matcher = Pattern.compile("(\\d+((\\/\\d+)?|(\\.\\d)?)+)").matcher(strMeasure);
						while (matcher.find()) measure.add(matcher.group());
						var searchResults = ingredientRepository.searchByRelevance(name);
						var ingredient = !searchResults.isEmpty() ? searchResults.getFirst() : null;

						String measureUnit = strMeasure.replaceAll("(\\d+((\\/\\d+)?|(\\.\\d)?)+)", "").trim();
						if (measureUnit.isBlank()) measureUnit = "piece";
						float measureAmount = measure.stream()
							.map(s -> toNumber(s))
							.filter(n -> n != null)
							.reduce((a, b) -> a.floatValue() + b.floatValue())
							.orElse(0f).floatValue();
						ingredients.add(new RecipeIngredientDto(
							ingredient != null ? ingredient.getId() : null,
							ingredient != null ? ingredient.getName() : name,
							measureAmount,
							measureUnit
						));
					} catch (Exception e) {
						System.err.println(e.getMessage());
						// Ignore missing/invalid ingredients
					}
				}

				recipes.add(new ExternalRecipeDto(
					recipe.get("strMeal").toString(),
					recipe.get("strSource") != null ? recipe.get("strSource").toString() : null,
					recipe.get("strInstructions").toString(),
					null,
					0,
					0,
					recipe.get("strMealThumb").toString(),
					ingredients,
					recipe.get("strSource") != null ? recipe.get("strSource").toString() : null
				));
			}

			return recipes;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch recipes from external API");
		}
	}
}