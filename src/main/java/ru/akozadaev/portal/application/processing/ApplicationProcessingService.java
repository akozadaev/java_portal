package ru.akozadaev.portal.application.processing;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.akozadaev.portal.application.kafka.ApplicationProcessingEvent;
import ru.akozadaev.portal.application.kafka.ApplicationProcessingProducer;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;
import ru.akozadaev.portal.application.repository.ApplicationRepository;

@Service
public class ApplicationProcessingService {

	private static final Logger log = LoggerFactory.getLogger(ApplicationProcessingService.class);

	private final ApplicationRepository applicationRepository;
	private final ApplicationProcessingProducer applicationProcessingProducer;
	private final int batchSize;

	public ApplicationProcessingService(
			ApplicationRepository applicationRepository,
			ApplicationProcessingProducer applicationProcessingProducer,
			@Value("${portal.application-processing.batch-size:50}") int batchSize) {
		this.applicationRepository = applicationRepository;
		this.applicationProcessingProducer = applicationProcessingProducer;
		this.batchSize = batchSize;
	}

	/**
	 * Захватывает пачку новых обращений и переводит их в статус PROCESSING.
	 *
	 * @return количество захваченных обращений
	 */
	@Transactional
	public int processNewApplicationsBatch() {
		List<ApplicationEntity> applications = applicationRepository.findBatchForProcessing(
				ApplicationStatus.NEW.name(),
				batchSize);

		applications.forEach(application -> application.setStatus(ApplicationStatus.PROCESSING));
		publishAfterCommit(applications);

		log.info("Fetched {} records with SKIP LOCKED", applications.size());

		return applications.size();
	}

	private void publishAfterCommit(List<ApplicationEntity> applications) {
		List<ApplicationProcessingEvent> events = applications.stream()
				.map(ApplicationProcessingEvent::from)
				.toList();

		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			events.forEach(applicationProcessingProducer::send);
			return;
		}

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				events.forEach(applicationProcessingProducer::send);
			}
		});
	}
}
