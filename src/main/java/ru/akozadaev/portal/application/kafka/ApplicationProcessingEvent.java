package ru.akozadaev.portal.application.kafka;

import java.time.Instant;
import java.util.UUID;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;

public record ApplicationProcessingEvent(
		UUID id,
		String fullName,
		String phone,
		String email,
		String text,
		ApplicationStatus status,
		Instant createdAt,
		Instant occurredAt) {

	public static ApplicationProcessingEvent from(ApplicationEntity entity) {
		return new ApplicationProcessingEvent(
				entity.getId(),
				entity.getFullName(),
				entity.getPhone(),
				entity.getEmail(),
				entity.getText(),
				entity.getStatus(),
				entity.getCreatedAt(),
				Instant.now());
	}
}
