package ru.akozadaev.portal.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akozadaev.portal.application.dto.ApplicationResponse;
import ru.akozadaev.portal.application.dto.CreateApplicationRequest;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;
import ru.akozadaev.portal.application.repository.ApplicationRepository;

@Service
public class ApplicationService {

	private final ApplicationRepository applicationRepository;

	public ApplicationService(ApplicationRepository applicationRepository) {
		this.applicationRepository = applicationRepository;
	}

	/**
	 * Создает обращение гражданина с нормализованным телефоном и статусом NEW.
	 *
	 * @param request пользовательские данные из REST API
	 * @return сохраненное обращение
	 * @throws IllegalArgumentException если нормализованный телефон имеет неподдерживаемый формат
	 */
	@Transactional
	public ApplicationResponse create(CreateApplicationRequest request) {
		String phone = normalizePhone(request.phone());
		ApplicationEntity entity = new ApplicationEntity(request.fullName(), phone, ApplicationStatus.NEW);

		return ApplicationResponse.from(applicationRepository.saveAndFlush(entity));
	}

	private String normalizePhone(String phone) {
		String normalized = phone.replaceAll("[\\s-]+", "");

		if (normalized.matches("^8\\d{10}$")) {
			normalized = "+7" + normalized.substring(1);
		}

		if (!normalized.matches("^\\+7\\d{10}$")) {
			throw new IllegalArgumentException("Телефон должен содержать 11 цифр и начинаться с +7 или 8");
		}

		return normalized;
	}
}
