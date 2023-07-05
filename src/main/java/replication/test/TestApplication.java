package replication.test;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import replication.test.config.properties.DataSourceProperties;

@SpringBootApplication
@RequiredArgsConstructor
public class TestApplication implements CommandLineRunner {

	private final DataSourceProperties properties;

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("properties = " + properties);
	}
}
