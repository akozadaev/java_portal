package ru.akozadaev.portal.application.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.akozadaev.portal.application.dto.ApplicationResponse;
import ru.akozadaev.portal.application.dto.CreateApplicationRequest;
import ru.akozadaev.portal.application.service.ApplicationService;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

	private final ApplicationService applicationService;

	public ApplicationController(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	/**
	 * Создает новое обращение гражданина.
	 *
	 * @param request тело запроса с данными заявителя
	 * @return созданное обращение и заголовок Location
	 */
	@PostMapping
	public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody CreateApplicationRequest request) {
		ApplicationResponse response = applicationService.create(request);
		URI location = URI.create("/api/v1/applications/" + response.id());

		return ResponseEntity.created(location).body(response);
	}
}
