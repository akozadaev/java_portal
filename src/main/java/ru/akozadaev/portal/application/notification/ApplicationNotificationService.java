package ru.akozadaev.portal.application.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.akozadaev.portal.application.model.ApplicationEntity;

@Service
public class ApplicationNotificationService {

	private static final String COMPLETED_SUBJECT = "Заявка обработана";
	private static final String COMPLETED_TEXT = "Ваша заявка обработана";

	private final JavaMailSender mailSender;

	public ApplicationNotificationService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * Отправляет заявителю письмо о завершении обработки обращения.
	 *
	 * @param application обработанное обращение
	 */
	public void sendCompleted(ApplicationEntity application) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(application.getEmail());
		message.setSubject(COMPLETED_SUBJECT);
		message.setText(COMPLETED_TEXT);

		mailSender.send(message);
	}
}
