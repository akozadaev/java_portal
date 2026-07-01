package ru.akozadaev.portal.application.exception;

import java.util.UUID;

public class ApplicationNotFoundException extends RuntimeException {

	public ApplicationNotFoundException(UUID id) {
		super("Обращение не найдено: " + id);
	}
}
