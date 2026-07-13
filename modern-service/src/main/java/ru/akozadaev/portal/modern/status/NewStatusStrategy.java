package ru.akozadaev.portal.modern.status;

public class NewStatusStrategy implements StatusUpdateStrategy {

	@Override
	public boolean supports(DocumentStatus status) {
		return status == DocumentStatus.NEW;
	}

	@Override
	public StatusUpdateResult update(StatusUpdateCommand command) {
		if (!command.documentsValid()) {
			return new StatusUpdateResult(
					DocumentStatus.ERROR,
					"Documents are invalid",
					"Document " + command.documentId() + " has invalid data");
		}

		if (!command.smevAvailable()) {
			return new StatusUpdateResult(
					DocumentStatus.ERROR,
					"Documents accepted for validation; SMEV is unavailable",
					"SMEV unavailable for document " + command.documentId());
		}

		if (!command.operatorApproved()) {
			return new StatusUpdateResult(
					DocumentStatus.ERROR,
					"Documents accepted for validation; sent to SMEV; operator rejected document",
					"Document " + command.documentId() + " was rejected");
		}

		return new StatusUpdateResult(
				DocumentStatus.COMPLETED,
				"Documents accepted for validation; sent to SMEV; completed by operator",
				"Document " + command.documentId() + " was completed");
	}
}
