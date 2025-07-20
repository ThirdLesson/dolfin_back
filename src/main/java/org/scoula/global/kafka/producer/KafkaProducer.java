package org.scoula.global.kafka.producer;

import org.scoula.global.kafka.dto.LogMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
@Slf4j
public class KafkaProducer {

	@Value("${spring.kafka.log-topic}")
	private String logTopic;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final KafkaTemplate<String, String> kafkaTemplate;

	public void sendToLogTopic(LogMessage logMessage) {
		try {
			String message = objectMapper.writeValueAsString(logMessage);
			kafkaTemplate.send(logTopic, message);
			System.out.println("로그 전송 됨 ????ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
