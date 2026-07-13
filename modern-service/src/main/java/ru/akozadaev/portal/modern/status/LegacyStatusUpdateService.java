package ru.akozadaev.portal.modern.status;

public class LegacyStatusUpdateService {

	private final StatusUpdateService statusUpdateService;

	public LegacyStatusUpdateService(StatusUpdateService statusUpdateService) {
		this.statusUpdateService = statusUpdateService;
	}

	public StatusUpdateResult updateStatus(
			String documentId,
			String status,
			boolean documentsValid,
			boolean smevAvailable,
			boolean operatorApproved) {
		if (documentId == null || documentId.isBlank()) {
			return new StatusUpdateResult(
					DocumentStatus.ERROR,
					"Document id is empty",
					"");
		}

		if (!DocumentStatus.isKnownLegacyValue(status)) {
			return new StatusUpdateResult(
					DocumentStatus.ERROR,
					"Unknown status",
					"Unknown status for document " + documentId);
		}

		if ("ERROR".equals(status)) {
			return new StatusUpdateResult(
					DocumentStatus.ERROR,
					"Unknown status",
					"Unknown status for document " + documentId);
		}

		StatusUpdateCommand command = new StatusUpdateCommand(
				documentId,
				DocumentStatus.fromLegacyValue(status),
				documentsValid,
				smevAvailable,
				operatorApproved);

		return statusUpdateService.update(command);
	}
}
