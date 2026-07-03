package ru.akozadaev.portal.application.controller;

import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.akozadaev.portal.application.dto.ApplicationResponse;
import ru.akozadaev.portal.application.dto.CreateApplicationRequest;
import ru.akozadaev.portal.application.exception.ApplicationNotFoundException;
import ru.akozadaev.portal.application.model.ApplicationStatus;
import ru.akozadaev.portal.application.service.ApplicationService;
import ru.akozadaev.portal.common.api.ApiExceptionHandler;
import ru.akozadaev.portal.config.SecurityConfig;

@WebMvcTest(ApplicationController.class)
@Import({ApiExceptionHandler.class, SecurityConfig.class})
class ApplicationControllerTest {

	private static final Instant NOW = Instant.parse("2026-07-01T12:00:00Z");

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ApplicationService applicationService;

	@Test
	void createReturnsCreatedApplication() throws Exception {
		UUID id = UUID.randomUUID();
		when(applicationService.create(any(CreateApplicationRequest.class))).thenReturn(response(id, ApplicationStatus.NEW));

		mockMvc.perform(post("/api/v1/applications")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "fullName": "Иван Иванов",
								  "phone": "8-999-000-11-22",
								  "text": "Текст обращения"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/v1/applications/" + id))
				.andExpect(jsonPath("$.id").value(id.toString()))
				.andExpect(jsonPath("$.status").value("NEW"))
				.andExpect(jsonPath("$.text").value("Текст обращения"));
	}

	@Test
	void createReturnsValidationErrors() throws Exception {
		mockMvc.perform(post("/api/v1/applications")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "fullName": "",
								  "phone": "123",
								  "text": ""
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Ошибка валидации запроса"))
				.andExpect(jsonPath("$.details", hasKey("fullName")))
				.andExpect(jsonPath("$.details", hasKey("phone")))
				.andExpect(jsonPath("$.details", hasKey("text")));
	}

	@Test
	void getByIdReturnsApplication() throws Exception {
		UUID id = UUID.randomUUID();
		when(applicationService.getById(id)).thenReturn(response(id, ApplicationStatus.COMPLETED));

		mockMvc.perform(get("/api/v1/applications/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id.toString()))
				.andExpect(jsonPath("$.status").value("COMPLETED"))
				.andExpect(jsonPath("$.processedAt").value(NOW.toString()));
	}

	@Test
	void getByIdReturnsNotFound() throws Exception {
		UUID id = UUID.randomUUID();
		when(applicationService.getById(id)).thenThrow(new ApplicationNotFoundException(id));

		mockMvc.perform(get("/api/v1/applications/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Обращение не найдено: " + id));
	}

	@Test
	void findAllReturnsApplications() throws Exception {
		UUID id = UUID.randomUUID();
		when(applicationService.findAll(null)).thenReturn(List.of(response(id, ApplicationStatus.NEW)));

		mockMvc.perform(get("/api/v1/applications"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(id.toString()))
				.andExpect(jsonPath("$[0].status").value("NEW"));
	}

	@Test
	void findAllFiltersByStatus() throws Exception {
		UUID id = UUID.randomUUID();
		when(applicationService.findAll(ApplicationStatus.COMPLETED))
				.thenReturn(List.of(response(id, ApplicationStatus.COMPLETED)));

		mockMvc.perform(get("/api/v1/applications").param("status", "COMPLETED"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].status").value("COMPLETED"));

		verify(applicationService).findAll(ApplicationStatus.COMPLETED);
	}

	@Test
	void findAllReturnsBadRequestForUnknownStatus() throws Exception {
		mockMvc.perform(get("/api/v1/applications").param("status", "UNKNOWN"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Некорректное значение параметра: status"))
				.andExpect(jsonPath("$.details.status").value("Передано значение: UNKNOWN"));
	}

	private ApplicationResponse response(UUID id, ApplicationStatus status) {
		return new ApplicationResponse(
				id,
				"Иван Иванов",
				"+79990001122",
				"Текст обращения",
				status,
				NOW,
				NOW,
				NOW);
	}
}
