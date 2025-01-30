package org.springframework.samples.petclinic.test.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetClinicE2ETest {

	private WebDriver driver;

	@BeforeAll
	void setupDriver() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	}

	@BeforeEach
	void setUp() {
		driver.get("https://spring-framework-petclinic-qctjpkmzuq-od.a.run.app");
	}

	@Test
	@Order(1)
	void shouldListOwners() {
		driver.findElement(By.linkText("Find owners")).click();
		driver.findElement(By.xpath("//button[text()='Find Owner']")).click();

		List<WebElement> owners = driver.findElements(By.cssSelector("table.table tbody tr"));
		assertThat(owners).isNotEmpty();
	}

	@Test
	@Order(2)
	void shouldRegisterNewOwner() {
		driver.findElement(By.linkText("Find owners")).click();
		driver.findElement(By.linkText("Add Owner")).click();

		driver.findElement(By.id("firstName")).sendKeys("John");
		driver.findElement(By.id("lastName")).sendKeys("Doe");
		driver.findElement(By.id("address")).sendKeys("123 Pet Street");
		driver.findElement(By.id("city")).sendKeys("PetCity");
		driver.findElement(By.id("telephone")).sendKeys("1234567890");
		driver.findElement(By.cssSelector("button[type='submit']")).click();

		String ownerName = driver.findElement(By.tagName("h2")).getText();
		assertThat(ownerName).contains("John Doe");
	}

	@AfterAll
	void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

}
