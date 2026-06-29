package ru.akozadaev.portal.application.dto;

import java.time.Instant;
import java.util.UUID;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;

public record ApplicationResponse(
		UUID id,
		String fullName,
		String phone,
		ApplicationStatus status,
		Instant createdAt,
		Instant updatedAt) {

	public static ApplicationResponse from(ApplicationEntity entity) {
		return new ApplicationResponse(
				entity.getId(),
				entity.getFullName(),
				entity.getPhone(),
				entity.getStatus(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}
}
