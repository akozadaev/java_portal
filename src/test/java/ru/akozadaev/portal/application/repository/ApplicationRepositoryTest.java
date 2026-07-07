package ru.akozadaev.portal.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;

@DataJpaTest
@Tag("integration")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApplicationRepositoryTest {

	@Container
	private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:15"));

	@Autowired
	private ApplicationRepository applicationRepository;

	@DynamicPropertySource
	static void postgresProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRES::getUsername);
		registry.add("spring.datasource.password", POSTGRES::getPassword);
		registry.add("spring.liquibase.change-log", () -> "classpath:/db/changelog/db.changelog-master.yml");
	}

	@Test
	void findAllByStatusOrderByCreatedAtAscFiltersApplications() {
		applicationRepository.save(new ApplicationEntity(
				"Иван Иванов",
				"+79990001122",
				"ivan@example.com",
				"Первое",
				ApplicationStatus.NEW));
		applicationRepository.save(new ApplicationEntity(
				"Петр Петров",
				"+79990002233",
				"petr@example.com",
				"Второе",
				ApplicationStatus.COMPLETED));
		applicationRepository.flush();

		List<ApplicationEntity> applications = applicationRepository.findAllByStatusOrderByCreatedAtAsc(
				ApplicationStatus.COMPLETED);

		assertThat(applications)
				.singleElement()
				.extracting(ApplicationEntity::getStatus)
				.isEqualTo(ApplicationStatus.COMPLETED);
	}

	@Test
	void findBatchForProcessingLocksOnlyRequestedStatus() {
		applicationRepository.save(new ApplicationEntity(
				"Иван Иванов",
				"+79990001122",
				"ivan@example.com",
				"Первое",
				ApplicationStatus.NEW));
		applicationRepository.save(new ApplicationEntity(
				"Петр Петров",
				"+79990002233",
				"petr@example.com",
				"Второе",
				ApplicationStatus.COMPLETED));
		applicationRepository.flush();

		List<ApplicationEntity> batch = applicationRepository.findBatchForProcessing(ApplicationStatus.NEW.name(), 50);

		assertThat(batch)
				.singleElement()
				.extracting(ApplicationEntity::getStatus)
				.isEqualTo(ApplicationStatus.NEW);
	}
}
