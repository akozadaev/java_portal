package ru.akozadaev.portal.application.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.akozadaev.portal.application.dto.ApplicationResponse;
import ru.akozadaev.portal.application.dto.CreateApplicationRequest;
import ru.akozadaev.portal.application.model.ApplicationStatus;
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

	/**
	 * Возвращает обращение по идентификатору.
	 *
	 * @param id идентификатор обращения
	 * @return найденное обращение
	 */
	@GetMapping("/{id}")
	public ApplicationResponse getById(@PathVariable UUID id) {
		return applicationService.getById(id);
	}

	/**
	 * Возвращает список обращений с необязательным фильтром по статусу.
	 *
	 * @param status статус обращения
	 * @return список обращений
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ApplicationResponse> findAll(@RequestParam(required = false) ApplicationStatus status) {
		return applicationService.findAll(status);
	}
}
