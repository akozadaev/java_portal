package ru.akozadaev.portal.legacy;

/**
 * Intentionally bad legacy code used as a refactoring target.
 */
public class OldStatusUpdater {

	public UpdateResult updateStatus(String id, String status, boolean documentsValid, boolean smevAvailable,
			boolean operatorApproved) {
		String sql = "SELECT * FROM docs WHERE id=" + id;
		String audit = "";
		String email = "";

		if (id == null || id.isBlank()) {
			status = "ERROR";
			audit = "Document id is empty";
			sql = sql + "; UPDATE docs SET status='ERROR' WHERE id=" + id;
		} else {
			if ("NEW".equals(status)) {
				if (documentsValid) {
					status = "VALIDATING";
					audit = "Documents accepted for validation";
					sql = sql + "; UPDATE docs SET status='VALIDATING' WHERE id=" + id;
					if (smevAvailable) {
						status = "SENT";
						audit = audit + "; sent to SMEV";
						sql = sql + "; UPDATE docs SET status='SENT' WHERE id=" + id;
						email = "Document " + id + " was sent to SMEV";
						if (operatorApproved) {
							status = "COMPLETED";
							audit = audit + "; completed by operator";
							sql = sql + "; UPDATE docs SET status='COMPLETED' WHERE id=" + id;
							email = "Document " + id + " was completed";
						} else {
							status = "ERROR";
							audit = audit + "; operator rejected document";
							sql = sql + "; UPDATE docs SET status='ERROR' WHERE id=" + id;
							email = "Document " + id + " was rejected";
						}
					} else {
						status = "ERROR";
						audit = audit + "; SMEV is unavailable";
						sql = sql + "; UPDATE docs SET status='ERROR' WHERE id=" + id;
						email = "SMEV unavailable for document " + id;
					}
				} else {
					status = "ERROR";
					audit = "Documents are invalid";
					sql = sql + "; UPDATE docs SET status='ERROR' WHERE id=" + id;
					email = "Document " + id + " has invalid data";
				}
			} else {
				if ("SENT".equals(status)) {
					if (operatorApproved) {
						status = "COMPLETED";
						audit = "Previously sent document completed";
						sql = sql + "; UPDATE docs SET status='COMPLETED' WHERE id=" + id;
						email = "Document " + id + " was completed";
					} else {
						status = "ERROR";
						audit = "Previously sent document rejected";
						sql = sql + "; UPDATE docs SET status='ERROR' WHERE id=" + id;
						email = "Document " + id + " was rejected";
					}
				} else {
					if ("COMPLETED".equals(status)) {
						audit = "No action for completed document";
						sql = sql + "; UPDATE docs SET status='COMPLETED' WHERE id=" + id;
					} else {
						status = "ERROR";
						audit = "Unknown status";
						sql = sql + "; UPDATE docs SET status='ERROR' WHERE id=" + id;
						email = "Unknown status for document " + id;
					}
				}
			}
		}

		return new UpdateResult(status, sql, audit, email);
	}

	public static class UpdateResult {
		private final String status;
		private final String sql;
		private final String audit;
		private final String email;

		public UpdateResult(String status, String sql, String audit, String email) {
			this.status = status;
			this.sql = sql;
			this.audit = audit;
			this.email = email;
		}

		public String getStatus() {
			return status;
		}

		public String getSql() {
			return sql;
		}

		public String getAudit() {
			return audit;
		}

		public String getEmail() {
			return email;
		}
	}
}
