package ru.akozadaev.portal.application.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProcessingProducer {

	private static final Logger log = LoggerFactory.getLogger(ApplicationProcessingProducer.class);

	private final KafkaTemplate<String, ApplicationProcessingEvent> kafkaTemplate;
	private final String topic;

	public ApplicationProcessingProducer(
			KafkaTemplate<String, ApplicationProcessingEvent> kafkaTemplate,
			@Value("${portal.kafka.topics.application-processing}") String topic) {
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
	}

	/**
	 * Отправляет событие о готовности обращения к обработке.
	 *
	 * @param event событие обработки обращения
	 */
	public void send(ApplicationProcessingEvent event) {
		kafkaTemplate.send(topic, event.id().toString(), event)
				.whenComplete((result, exception) -> {
					if (exception != null) {
						log.error("Failed to send application processing event: {}", event.id(), exception);
						return;
					}

					log.info("Sent application processing event: {}", event.id());
				});
	}
}
