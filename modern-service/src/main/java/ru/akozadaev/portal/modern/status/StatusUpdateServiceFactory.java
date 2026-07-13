package ru.akozadaev.portal.modern.status;

import java.util.List;

public final class StatusUpdateServiceFactory {

	private StatusUpdateServiceFactory() {
	}

	public static StatusUpdateService createDefault() {
		return new StatusUpdateService(List.of(
				new NewStatusStrategy(),
				new SentStatusStrategy(),
				new ValidatingStatusStrategy(),
				new TerminalStatusStrategy()));
	}

	public static LegacyStatusUpdateService createLegacyFacade() {
		return new LegacyStatusUpdateService(createDefault());
	}
}
