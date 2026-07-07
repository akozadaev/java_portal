package ru.akozadaev.portal.application.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akozadaev.portal.application.kafka.ApplicationProcessingEvent;
import ru.akozadaev.portal.application.kafka.ApplicationProcessingProducer;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;
import ru.akozadaev.portal.application.repository.ApplicationRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationProcessingServiceTest {

	@Mock
	private ApplicationRepository applicationRepository;

	@Mock
	private ApplicationProcessingProducer applicationProcessingProducer;

	@Test
	void processNewApplicationsBatchMovesApplicationsToProcessingAndPublishesEvents() {
		ApplicationEntity first = new ApplicationEntity(
				"Иван Иванов",
				"+79990001122",
				"ivan@example.com",
				"Первое",
				ApplicationStatus.NEW);
		ApplicationEntity second = new ApplicationEntity(
				"Петр Петров",
				"+79990002233",
				"petr@example.com",
				"Второе",
				ApplicationStatus.NEW);
		when(applicationRepository.findBatchForProcessing(ApplicationStatus.NEW.name(), 50))
				.thenReturn(List.of(first, second));
		ApplicationProcessingService service = new ApplicationProcessingService(
				applicationRepository,
				applicationProcessingProducer,
				50);

		int processedCount = service.processNewApplicationsBatch();

		assertThat(processedCount).isEqualTo(2);
		assertThat(first.getStatus()).isEqualTo(ApplicationStatus.PROCESSING);
		assertThat(second.getStatus()).isEqualTo(ApplicationStatus.PROCESSING);

		ArgumentCaptor<ApplicationProcessingEvent> eventCaptor = ArgumentCaptor.forClass(ApplicationProcessingEvent.class);
		verify(applicationProcessingProducer, times(2)).send(eventCaptor.capture());
		assertThat(eventCaptor.getAllValues())
				.extracting(ApplicationProcessingEvent::status)
				.containsOnly(ApplicationStatus.PROCESSING);
	}
}
