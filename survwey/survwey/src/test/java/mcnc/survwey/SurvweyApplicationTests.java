package mcnc.survwey;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SurvweyApplicationTests {
	@BeforeAll
	static void setup() {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("MAIL_USER_NAME", dotenv.get("MAIL_USER_NAME"));
		System.setProperty("MAIL_USER_PASSWORD", dotenv.get("MAIL_USER_PASSWORD"));
	}

	@Test
	void contextLoads() {
	}

}
