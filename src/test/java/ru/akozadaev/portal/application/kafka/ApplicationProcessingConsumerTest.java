package ru.akozadaev.portal.application.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;
import ru.akozadaev.portal.application.notification.ApplicationNotificationService;
import ru.akozadaev.portal.application.repository.ApplicationRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationProcessingConsumerTest {

	@Mock
	private ApplicationRepository applicationRepository;

	@Mock
	private ApplicationNotificationService notificationService;

	@Test
	void consumeCompletesProcessingApplication() {
		UUID id = UUID.randomUUID();
		ApplicationEntity entity = new ApplicationEntity(
				"Иван Иванов",
				"+79990001122",
				"ivan@example.com",
				"Текст обращения",
				ApplicationStatus.PROCESSING);
		when(applicationRepository.findById(id)).thenReturn(Optional.of(entity));
		ApplicationProcessingConsumer consumer = new ApplicationProcessingConsumer(applicationRepository, notificationService);

		consumer.consume(event(id));

		assertThat(entity.getStatus()).isEqualTo(ApplicationStatus.COMPLETED);
		assertThat(entity.getProcessedAt()).isNotNull();
		verify(notificationService).sendCompleted(entity);
	}

	@Test
	void consumeSkipsApplicationWithUnexpectedStatus() {
		UUID id = UUID.randomUUID();
		ApplicationEntity entity = new ApplicationEntity(
				"Иван Иванов",
				"+79990001122",
				"ivan@example.com",
				"Текст обращения",
				ApplicationStatus.NEW);
		when(applicationRepository.findById(id)).thenReturn(Optional.of(entity));
		ApplicationProcessingConsumer consumer = new ApplicationProcessingConsumer(applicationRepository, notificationService);

		consumer.consume(event(id));

		assertThat(entity.getStatus()).isEqualTo(ApplicationStatus.NEW);
		assertThat(entity.getProcessedAt()).isNull();
		verify(notificationService, never()).sendCompleted(entity);
	}

	@Test
	void consumeIgnoresMissingApplication() {
		UUID id = UUID.randomUUID();
		when(applicationRepository.findById(id)).thenReturn(Optional.empty());
		ApplicationProcessingConsumer consumer = new ApplicationProcessingConsumer(applicationRepository, notificationService);

		consumer.consume(event(id));

		verify(applicationRepository).findById(id);
		verify(applicationRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
		verify(notificationService, never()).sendCompleted(org.mockito.ArgumentMatchers.any());
	}

	private ApplicationProcessingEvent event(UUID id) {
		Instant now = Instant.parse("2026-07-01T12:00:00Z");
		return new ApplicationProcessingEvent(
				id,
				"Иван Иванов",
				"+79990001122",
				"ivan@example.com",
				"Текст обращения",
				ApplicationStatus.PROCESSING,
				now,
				now);
	}
}
