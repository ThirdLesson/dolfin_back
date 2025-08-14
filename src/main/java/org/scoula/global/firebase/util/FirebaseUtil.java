package org.scoula.global.firebase.util;

import java.util.List;

import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.kafka.producer.KafkaProducer;
import org.scoula.global.kafka.util.LogMessageMapper;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FirebaseUtil {

	private final KafkaProducer kafkaProducer;

	public void sendNotices(List<String> tokens, String title, String body) {
		for (String token : tokens) {
			Message message = Message.builder()
				.setToken(token)
				.putData("title", title)
				.putData("body", body)
				.setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "1000")
					.setNotification(new WebpushNotification(title, body))
					.build())
				.build();
			try {
				String send = FirebaseMessaging.getInstance().send(message);
			} catch (Exception e) {
				kafkaProducer.sendToLogTopic(
					LogMessageMapper.buildLogMessage(LogLevel.INFO, null, "fcm 알림 전송 실패", Common.builder().build(),
						e.toString()));
			}
		}
	}

	public void sendNotice(String token, String title, String body) {
		Message message = Message.builder()
			.setToken(token)
			.putData("title", title)
			.putData("body", body)
			.setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "1000")
				.setNotification(new WebpushNotification(title, body))
				.build())
			.build();
		try {
			String send = FirebaseMessaging.getInstance().send(message);
		} catch (Exception e) {
			kafkaProducer.sendToLogTopic(
				LogMessageMapper.buildLogMessage(LogLevel.INFO, null, "fcm 알림 전송 실패", Common.builder().build(),
					e.toString()));
		}
	}
}
