package ru.akozadaev.portal.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateApplicationRequest(
		@NotBlank(message = "ФИО обязательно для заполнения")
		String fullName,

		@NotBlank(message = "Телефон обязателен для заполнения")
		@Pattern(
				regexp = "^(?:\\+7|8)[0-9\\s-]{10,20}$",
				message = "Телефон должен начинаться с +7 или 8 и содержать 11 цифр")
		String phone) {
}
