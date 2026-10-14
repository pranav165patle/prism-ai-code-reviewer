
package AI.PR.Reviewer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class AiPrReviewerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainShouldInvokeSpringApplicationRun() {
		String[] args = {"--spring.main.web-application-type=none"};

		try (var mockedSpring = mockStatic(SpringApplication.class)) {
			assertDoesNotThrow(() -> AiPrReviewerApplication.main(args));

			mockedSpring.verify(() ->
					SpringApplication.run(AiPrReviewerApplication.class, args));
		}
	}
}
