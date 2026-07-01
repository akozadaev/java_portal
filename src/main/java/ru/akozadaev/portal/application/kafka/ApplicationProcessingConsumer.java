package ru.akozadaev.portal.application.kafka;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;
import ru.akozadaev.portal.application.repository.ApplicationRepository;

@Component
@ConditionalOnProperty(prefix = "portal.kafka.consumer", name = "enabled", havingValue = "true")
public class ApplicationProcessingConsumer {

	private static final Logger log = LoggerFactory.getLogger(ApplicationProcessingConsumer.class);

	private final ApplicationRepository applicationRepository;

	public ApplicationProcessingConsumer(ApplicationRepository applicationRepository) {
		this.applicationRepository = applicationRepository;
	}

	/**
	 * Обрабатывает событие из Kafka и завершает обращение.
	 *
	 * @param event событие обработки обращения
	 */
	@Transactional
	@KafkaListener(topics = "${portal.kafka.topics.application-processing}")
	public void consume(ApplicationProcessingEvent event) {
		applicationRepository.findById(event.id())
				.ifPresentOrElse(
						this::completeApplication,
						() -> log.warn("Application not found for processing event: {}", event.id()));
	}

	private void completeApplication(ApplicationEntity application) {
		if (application.getStatus() != ApplicationStatus.PROCESSING) {
			log.warn(
					"Skip application completion because status is {}: {}",
					application.getStatus(),
					application.getId());
			return;
		}

		application.setStatus(ApplicationStatus.COMPLETED);
		application.setProcessedAt(Instant.now());

		log.info("Completed application from Kafka event: {}", application.getId());
	}
}
