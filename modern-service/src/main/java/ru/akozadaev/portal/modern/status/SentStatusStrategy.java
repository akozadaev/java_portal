package ru.akozadaev.portal.modern.status;

public class SentStatusStrategy implements StatusUpdateStrategy {

	@Override
	public boolean supports(DocumentStatus status) {
		return status == DocumentStatus.SENT;
	}

	@Override
	public StatusUpdateResult update(StatusUpdateCommand command) {
		if (command.operatorApproved()) {
			return new StatusUpdateResult(
					DocumentStatus.COMPLETED,
					"Previously sent document completed",
					"Document " + command.documentId() + " was completed");
		}

		return new StatusUpdateResult(
				DocumentStatus.ERROR,
				"Previously sent document rejected",
				"Document " + command.documentId() + " was rejected");
	}
}
