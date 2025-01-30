package org.springframework.samples.petclinic.test;

import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class PetstoreIntegrationTests {

	private static final String BASE_URL = "https://petstore3.swagger.io/api/v3/pet";

	@Test
	public void shouldGetPetById() {
		int petId = 1;

		SerenityRest.given()
			.baseUri(BASE_URL)
			.basePath("/{petId}")
			.pathParam("petId", petId)
			.when()
			.get()
			.then()
			.statusCode(200)
			.body("id", equalTo(petId))
			.body("name", notNullValue())
			.body("status", notNullValue());
	}


	@Test
	public void shouldCreatePet() {
		String newPet = "{\n" +
			"  \"name\": \"Fluffy\",\n" +
			"  \"status\": \"available\",\n" +
			"  \"category\": {\n" +
			"    \"id\": 0,\n" +
			"    \"name\": \"dog\"\n" +
			"  }\n" +
			"}";

		SerenityRest.given()
			.baseUri(BASE_URL)
			.header("Content-Type", "application/json")
			.body(newPet)
			.when()
			.post()
			.then()
			.statusCode(200)
			.body("name", equalTo("Fluffy"))
			.body("status", equalTo("available"));
	}

}
