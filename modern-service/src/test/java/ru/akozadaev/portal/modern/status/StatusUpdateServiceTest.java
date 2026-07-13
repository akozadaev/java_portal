package ru.akozadaev.portal.modern.status;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusUpdateServiceTest {

	private final StatusUpdateService service = StatusUpdateServiceFactory.createDefault();

	@Test
	void completesNewDocumentWhenAllChecksPass() {
		StatusUpdateResult result = service.update(new StatusUpdateCommand(
				"42",
				DocumentStatus.NEW,
				true,
				true,
				true));

		assertThat(result.status()).isEqualTo(DocumentStatus.COMPLETED);
		assertThat(result.audit()).isEqualTo("Documents accepted for validation; sent to SMEV; completed by operator");
		assertThat(result.notification()).isEqualTo("Document 42 was completed");
	}

	@Test
	void rejectsNewDocumentWhenDocumentsAreInvalid() {
		StatusUpdateResult result = service.update(new StatusUpdateCommand(
				"77",
				DocumentStatus.NEW,
				false,
				true,
				true));

		assertThat(result.status()).isEqualTo(DocumentStatus.ERROR);
		assertThat(result.audit()).isEqualTo("Documents are invalid");
		assertThat(result.notification()).isEqualTo("Document 77 has invalid data");
	}

	@Test
	void rejectsSentDocumentWhenOperatorDoesNotApproveIt() {
		StatusUpdateResult result = service.update(new StatusUpdateCommand(
				"100",
				DocumentStatus.SENT,
				true,
				true,
				false));

		assertThat(result.status()).isEqualTo(DocumentStatus.ERROR);
		assertThat(result.audit()).isEqualTo("Previously sent document rejected");
		assertThat(result.notification()).isEqualTo("Document 100 was rejected");
	}

	@Test
	void rejectsEmptyDocumentId() {
		StatusUpdateResult result = service.update(new StatusUpdateCommand(
				" ",
				DocumentStatus.NEW,
				true,
				true,
				true));

		assertThat(result.status()).isEqualTo(DocumentStatus.ERROR);
		assertThat(result.audit()).isEqualTo("Document id is empty");
		assertThat(result.notification()).isEmpty();
	}

	@Test
	void treatsValidatingStatusAsUnsupportedLegacyState() {
		StatusUpdateResult result = service.update(new StatusUpdateCommand(
				"55",
				DocumentStatus.VALIDATING,
				true,
				true,
				true));

		assertThat(result.status()).isEqualTo(DocumentStatus.ERROR);
		assertThat(result.audit()).isEqualTo("Unknown status");
		assertThat(result.notification()).isEqualTo("Unknown status for document 55");
	}
}
