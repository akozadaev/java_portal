package ru.akozadaev.portal.application.processing;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;
import ru.akozadaev.portal.application.repository.ApplicationRepository;

@Service
public class ApplicationProcessingService {

	private static final Logger log = LoggerFactory.getLogger(ApplicationProcessingService.class);

	private final ApplicationRepository applicationRepository;
	private final int batchSize;

	public ApplicationProcessingService(
			ApplicationRepository applicationRepository,
			@Value("${portal.application-processing.batch-size:50}") int batchSize) {
		this.applicationRepository = applicationRepository;
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

		log.info("Fetched {} records with SKIP LOCKED", applications.size());

		return applications.size();
	}
}
