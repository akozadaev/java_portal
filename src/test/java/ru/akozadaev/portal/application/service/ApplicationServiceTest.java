package ru.akozadaev.portal.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.akozadaev.portal.application.dto.ApplicationResponse;
import ru.akozadaev.portal.application.dto.CreateApplicationRequest;
import ru.akozadaev.portal.application.exception.ApplicationNotFoundException;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;
import ru.akozadaev.portal.application.repository.ApplicationRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

	@Mock
	private ApplicationRepository applicationRepository;

	@InjectMocks
	private ApplicationService applicationService;

	@Test
	void createNormalizesPhoneAndCreatesNewApplication() {
		CreateApplicationRequest request = new CreateApplicationRequest(
				"Иван Иванов",
				"8-999-000-11-22",
				"Текст обращения");
		when(applicationRepository.saveAndFlush(any(ApplicationEntity.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ApplicationResponse response = applicationService.create(request);

		ArgumentCaptor<ApplicationEntity> entityCaptor = ArgumentCaptor.forClass(ApplicationEntity.class);
		verify(applicationRepository).saveAndFlush(entityCaptor.capture());
		ApplicationEntity savedEntity = entityCaptor.getValue();

		assertThat(savedEntity.getFullName()).isEqualTo("Иван Иванов");
		assertThat(savedEntity.getPhone()).isEqualTo("+79990001122");
		assertThat(savedEntity.getText()).isEqualTo("Текст обращения");
		assertThat(savedEntity.getStatus()).isEqualTo(ApplicationStatus.NEW);
		assertThat(response.phone()).isEqualTo("+79990001122");
		assertThat(response.status()).isEqualTo(ApplicationStatus.NEW);
	}

	@Test
	void createRejectsUnsupportedPhoneFormat() {
		CreateApplicationRequest request = new CreateApplicationRequest(
				"Иван Иванов",
				"123",
				"Текст обращения");

		assertThatThrownBy(() -> applicationService.create(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Телефон должен содержать 11 цифр и начинаться с +7 или 8");

		verify(applicationRepository, never()).saveAndFlush(any());
	}

	@Test
	void getByIdReturnsApplication() {
		UUID id = UUID.randomUUID();
		ApplicationEntity entity = new ApplicationEntity(
				"Иван Иванов",
				"+79990001122",
				"Текст обращения",
				ApplicationStatus.COMPLETED);
		when(applicationRepository.findById(id)).thenReturn(Optional.of(entity));

		ApplicationResponse response = applicationService.getById(id);

		assertThat(response.fullName()).isEqualTo("Иван Иванов");
		assertThat(response.status()).isEqualTo(ApplicationStatus.COMPLETED);
	}

	@Test
	void getByIdThrowsWhenApplicationDoesNotExist() {
		UUID id = UUID.randomUUID();
		when(applicationRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> applicationService.getById(id))
				.isInstanceOf(ApplicationNotFoundException.class)
				.hasMessage("Обращение не найдено: " + id);
	}

	@Test
	void findAllReturnsAllApplicationsWhenStatusIsNull() {
		ApplicationEntity first = new ApplicationEntity("Иван Иванов", "+79990001122", "Первое", ApplicationStatus.NEW);
		ApplicationEntity second = new ApplicationEntity("Петр Петров", "+79990002233", "Второе", ApplicationStatus.COMPLETED);
		when(applicationRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"))).thenReturn(List.of(first, second));

		List<ApplicationResponse> responses = applicationService.findAll(null);

		assertThat(responses).hasSize(2);
		verify(applicationRepository).findAll(Sort.by(Sort.Direction.ASC, "createdAt"));
	}

	@Test
	void findAllFiltersByStatus() {
		ApplicationEntity entity = new ApplicationEntity(
				"Петр Петров",
				"+79990002233",
				"Текст",
				ApplicationStatus.COMPLETED);
		when(applicationRepository.findAllByStatusOrderByCreatedAtAsc(ApplicationStatus.COMPLETED))
				.thenReturn(List.of(entity));

		List<ApplicationResponse> responses = applicationService.findAll(ApplicationStatus.COMPLETED);

		assertThat(responses)
				.singleElement()
				.extracting(ApplicationResponse::status)
				.isEqualTo(ApplicationStatus.COMPLETED);
	}
}
