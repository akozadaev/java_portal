package ru.akozadaev.portal.modern.status;

public record StatusUpdateCommand(
		String documentId,
		DocumentStatus currentStatus,
		boolean documentsValid,
		boolean smevAvailable,
		boolean operatorApproved) {
}
