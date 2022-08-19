package JiraTest.JiraTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletRegistration;

@SpringBootApplication
public class JiraTestApplication extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(JiraTestApplication.class);

	public static void main(String[] args) {
		log.info("starting...");
		SpringApplication.run(JiraTestApplication.class, args);
		log.info("started");
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(JiraTestApplication.class);
	}

}
