package ru.akozadaev.portal.modern.status;

public class ValidatingStatusStrategy implements StatusUpdateStrategy {

	@Override
	public boolean supports(DocumentStatus status) {
		return status == DocumentStatus.VALIDATING;
	}

	@Override
	public StatusUpdateResult update(StatusUpdateCommand command) {
		return new StatusUpdateResult(
				DocumentStatus.ERROR,
				"Unknown status",
				"Unknown status for document " + command.documentId());
	}
}
