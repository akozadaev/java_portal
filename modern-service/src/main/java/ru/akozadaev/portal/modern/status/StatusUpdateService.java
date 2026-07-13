package ru.akozadaev.portal.modern.status;

import java.util.List;

public class StatusUpdateService {

	private final List<StatusUpdateStrategy> strategies;

	public StatusUpdateService(List<StatusUpdateStrategy> strategies) {
		this.strategies = strategies;
	}

	public StatusUpdateResult update(StatusUpdateCommand command) {
		if (command.documentId() == null || command.documentId().isBlank()) {
			return new StatusUpdateResult(
					DocumentStatus.ERROR,
					"Document id is empty",
					"");
		}

		return strategies.stream()
				.filter(strategy -> strategy.supports(command.currentStatus()))
				.findFirst()
				.orElseGet(UnknownStatusStrategy::new)
				.update(command);
	}
}
