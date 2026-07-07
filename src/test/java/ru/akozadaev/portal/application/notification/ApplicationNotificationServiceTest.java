package ru.akozadaev.portal.application.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.akozadaev.portal.application.model.ApplicationEntity;
import ru.akozadaev.portal.application.model.ApplicationStatus;

@ExtendWith(MockitoExtension.class)
class ApplicationNotificationServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@Test
	void sendCompletedSendsEmailToApplicant() {
		ApplicationEntity application = new ApplicationEntity(
				"Иван Иванов",
				"+79990001122",
				"ivan@example.com",
				"Текст обращения",
				ApplicationStatus.COMPLETED);
		ApplicationNotificationService service = new ApplicationNotificationService(mailSender);

		service.sendCompleted(application);

		ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(messageCaptor.capture());
		SimpleMailMessage message = messageCaptor.getValue();

		assertThat(message.getTo()).containsExactly("ivan@example.com");
		assertThat(message.getSubject()).isEqualTo("Заявка обработана");
		assertThat(message.getText()).isEqualTo("Ваша заявка обработана");
	}
}
