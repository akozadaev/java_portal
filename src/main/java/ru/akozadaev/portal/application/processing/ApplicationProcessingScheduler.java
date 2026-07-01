package ru.akozadaev.portal.application.processing;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "portal.application-processing", name = "enabled", havingValue = "true")
public class ApplicationProcessingScheduler {

	private final ApplicationProcessingService applicationProcessingService;

	public ApplicationProcessingScheduler(ApplicationProcessingService applicationProcessingService) {
		this.applicationProcessingService = applicationProcessingService;
	}

	/**
	 * Запускает периодическую обработку новых обращений.
	 */
	@Scheduled(fixedDelayString = "${portal.application-processing.fixed-delay-ms:10000}")
	public void processNewApplications() {
		applicationProcessingService.processNewApplicationsBatch();
	}
}
