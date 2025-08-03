package org.scoula.global.firebase.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;

@Component
public class FirebaseUtil {
	public void sendNotice(List<String> tokens, String title, String body) {
		for (String token : tokens) {
			Message message = Message.builder()
				.setToken(token)
				.putData("title", title)
				.putData("body", body)
				.setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "1000")
					.setNotification(new WebpushNotification("title", "body"))
					.build())
				.build();

			FirebaseMessaging.getInstance().sendAsync(message);
		}
	}
}
