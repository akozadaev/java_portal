package ru.akozadaev.portal.modern.status;

public class TerminalStatusStrategy implements StatusUpdateStrategy {

	@Override
	public boolean supports(DocumentStatus status) {
		return status == DocumentStatus.COMPLETED || status == DocumentStatus.ERROR;
	}

	@Override
	public StatusUpdateResult update(StatusUpdateCommand command) {
		if (command.currentStatus() == DocumentStatus.COMPLETED) {
			return new StatusUpdateResult(
					DocumentStatus.COMPLETED,
					"No action for completed document",
					"");
		}

		return new StatusUpdateResult(
				DocumentStatus.ERROR,
				"No action for failed document",
				"");
	}
}
