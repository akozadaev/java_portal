package ru.akozadaev.portal.modern.status;

public record StatusUpdateResult(
		DocumentStatus status,
		String audit,
		String notification) {
}
