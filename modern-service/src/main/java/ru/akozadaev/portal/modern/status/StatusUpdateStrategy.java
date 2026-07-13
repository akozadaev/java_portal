package ru.akozadaev.portal.modern.status;

public interface StatusUpdateStrategy {

	boolean supports(DocumentStatus status);

	StatusUpdateResult update(StatusUpdateCommand command);
}
