package ru.akozadaev.portal.modern.status;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.akozadaev.portal.legacy.OldStatusUpdater;

class LegacyCompatibilityTest {

	private final OldStatusUpdater legacy = new OldStatusUpdater();
	private final LegacyStatusUpdateService modern = StatusUpdateServiceFactory.createLegacyFacade();

	@Test
	void keepsBehaviorForCompletedNewDocument() {
		assertSameBusinessResult("42", "NEW", true, true, true);
	}

	@Test
	void keepsBehaviorForInvalidNewDocument() {
		assertSameBusinessResult("77", "NEW", false, true, true);
	}

	@Test
	void keepsBehaviorForUnavailableSmev() {
		assertSameBusinessResult("88", "NEW", true, false, true);
	}

	@Test
	void keepsBehaviorForRejectedSentDocument() {
		assertSameBusinessResult("100", "SENT", true, true, false);
	}

	@Test
	void keepsBehaviorForCompletedDocument() {
		assertSameBusinessResult("101", "COMPLETED", true, true, true);
	}

	@Test
	void keepsBehaviorForValidatingDocument() {
		assertSameBusinessResult("102", "VALIDATING", true, true, true);
	}

	@Test
	void keepsBehaviorForUnknownStatus() {
		assertSameBusinessResult("103", "ARCHIVED", true, true, true);
	}

	@Test
	void keepsBehaviorForErrorStatus() {
		assertSameBusinessResult("104", "ERROR", true, true, true);
	}

	@Test
	void keepsBehaviorForEmptyDocumentId() {
		assertSameBusinessResult("", "NEW", true, true, true);
	}

	private void assertSameBusinessResult(
			String id,
			String status,
			boolean documentsValid,
			boolean smevAvailable,
			boolean operatorApproved) {
		OldStatusUpdater.UpdateResult legacyResult = legacy.updateStatus(
				id,
				status,
				documentsValid,
				smevAvailable,
				operatorApproved);
		StatusUpdateResult modernResult = modern.updateStatus(
				id,
				status,
				documentsValid,
				smevAvailable,
				operatorApproved);

		assertThat(modernResult.status().name()).isEqualTo(legacyResult.getStatus());
		assertThat(modernResult.audit()).isEqualTo(legacyResult.getAudit());
		assertThat(modernResult.notification()).isEqualTo(legacyResult.getEmail());
	}
}
