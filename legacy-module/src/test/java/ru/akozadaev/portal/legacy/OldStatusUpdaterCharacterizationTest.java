package ru.akozadaev.portal.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OldStatusUpdaterCharacterizationTest {

	private final OldStatusUpdater updater = new OldStatusUpdater();

	@Test
	void completesNewDocumentWhenAllExternalChecksPass() {
		OldStatusUpdater.UpdateResult result = updater.updateStatus("42", "NEW", true, true, true);

		assertEquals("COMPLETED", result.getStatus());
		assertEquals("Document 42 was completed", result.getEmail());
		assertTrue(result.getAudit().contains("sent to SMEV"));
		assertTrue(result.getSql().contains("SELECT * FROM docs WHERE id=42"));
		assertTrue(result.getSql().contains("UPDATE docs SET status='COMPLETED' WHERE id=42"));
	}

	@Test
	void rejectsNewDocumentWhenDocumentsAreInvalid() {
		OldStatusUpdater.UpdateResult result = updater.updateStatus("77", "NEW", false, true, true);

		assertEquals("ERROR", result.getStatus());
		assertEquals("Documents are invalid", result.getAudit());
		assertEquals("Document 77 has invalid data", result.getEmail());
		assertTrue(result.getSql().contains("UPDATE docs SET status='ERROR' WHERE id=77"));
	}

	@Test
	void rejectsSentDocumentWhenOperatorDoesNotApproveIt() {
		OldStatusUpdater.UpdateResult result = updater.updateStatus("100", "SENT", true, true, false);

		assertEquals("ERROR", result.getStatus());
		assertEquals("Previously sent document rejected", result.getAudit());
		assertEquals("Document 100 was rejected", result.getEmail());
		assertTrue(result.getSql().contains("UPDATE docs SET status='ERROR' WHERE id=100"));
	}
}
