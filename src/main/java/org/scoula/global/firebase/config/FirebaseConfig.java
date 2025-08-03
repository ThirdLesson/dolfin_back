package org.scoula.global.firebase.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@PropertySource("classpath:application.properties")
public class FirebaseConfig {

	@Value("${firebase.project.id}")
	String projectId;
	@Value("${firebase.client.email}")
	String clientEmail;
	@Value("${firebase.private.key.id}")
	String privateKeyId;
	@Value("${firebase.private.key}")
	String privateKeyPem;
	@Value("${firebase.client.id}")
	String clientId;

	@PostConstruct
	public void init() {

		String json = """
			{
			  "type": "service_account",
			  "project_id": "%s",
			  "private_key_id": "%s",
			  "private_key": "%s",
			  "client_email": "%s",
			  "client_id": "%s",
			  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
			  "token_uri": "https://oauth2.googleapis.com/token",
			  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
			  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/%s"
			}
			""".formatted(
			projectId, privateKeyId, privateKeyPem.replace("\\n", "\n"), clientEmail,
			clientId,
			clientEmail.replace("@", "%40")
		);

		try (InputStream is = new ByteArrayInputStream(json.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(is))
				.build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
				log.info("Firebase application init");
			}
		} catch (IOException e) {
			throw new Error(e);
		}
	}
}
