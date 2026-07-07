package ru.akozadaev.portal.application.kafka;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.akozadaev.portal.application.model.ApplicationStatus;

@ExtendWith(MockitoExtension.class)
class ApplicationProcessingProducerTest {

	@Mock
	private KafkaTemplate<String, ApplicationProcessingEvent> kafkaTemplate;

	@Test
	void sendPublishesEventByApplicationId() {
		ApplicationProcessingEvent event = event();
		when(kafkaTemplate.send("portal.application.processing", event.id().toString(), event))
				.thenReturn(CompletableFuture.completedFuture(new SendResult<>(null, null)));
		ApplicationProcessingProducer producer = new ApplicationProcessingProducer(
				kafkaTemplate,
				"portal.application.processing");

		producer.send(event);

		verify(kafkaTemplate).send("portal.application.processing", event.id().toString(), event);
	}

	private ApplicationProcessingEvent event() {
		Instant now = Instant.parse("2026-07-01T12:00:00Z");
		return new ApplicationProcessingEvent(
				UUID.randomUUID(),
				"Иван Иванов",
				"+79990001122",
				"ivan@example.com",
				"Текст обращения",
				ApplicationStatus.PROCESSING,
				now,
				now);
	}
}
