package ru.akozadaev.portal.modern.status;

public enum DocumentStatus {
	NEW,
	VALIDATING,
	SENT,
	COMPLETED,
	ERROR;

	public static boolean isKnownLegacyValue(String value) {
		return "NEW".equals(value)
				|| "VALIDATING".equals(value)
				|| "SENT".equals(value)
				|| "COMPLETED".equals(value)
				|| "ERROR".equals(value);
	}

	public static DocumentStatus fromLegacyValue(String value) {
		return switch (value) {
			case "NEW" -> NEW;
			case "VALIDATING" -> VALIDATING;
			case "SENT" -> SENT;
			case "COMPLETED" -> COMPLETED;
			default -> ERROR;
		};
	}
}
