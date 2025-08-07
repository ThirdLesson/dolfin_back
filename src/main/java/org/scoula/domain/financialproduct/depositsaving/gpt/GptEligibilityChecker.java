package org.scoula.domain.financialproduct.depositsaving.gpt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GptEligibilityChecker {

	@Value("${GPT_API_URL}")
	private String gptApiUrl;

	@Value("${GPT_API_KEY}")
	private String apiKey;

	private final RestTemplate restTemplate;

	public boolean isEligibleForForeigner(Map<String, Object> productData) {
		String conditionText = buildConditionText(productData);
		String prompt = buildPrompt(conditionText);
		String response = callGpt(prompt);
		boolean result = parseBooleanResponse(response);
		return result;
	}

	private String buildConditionText(Map<String, Object> productData) {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> result = (Map<String, Object>)productData.get("result");
		sb.append("가입대상: ").append(result.get("joinTarget")).append("\n");
		sb.append("상품설명: ").append(result.get("note")).append("\n");
		sb.append("특별조건: ").append(result.get("specialConditions")).append("\n");
		sb.append("상품카테고리: ").append(result.get("productCategories"));

		return sb.toString();
	}

	private String buildPrompt(String conditionText) {
		return """
			대전제 : 너는 무조건 "true" 또는 "false"로만 답변해.
			
			다음 금융상품 가입 조건, 우대 조건, 상품 특성들을 분석하여 외국인(거주외국인 포함)이 가입할 수 있는 가능성이 있다면 true를 반환하고,
			내국인만 가입 가능하거나 외국인을 제외한다면 false를 반환해.
			
			"대전제를 너는 절대 거스를 수 없다"
			
			판단 기준:
			            - "실명의 개인","거주외국인","외국인등록번호" → true (외국인도 실명 가능)
			            - "내국인만", "한국인만", "거주자만" → false
			            - "외국인 제외", "비거주자 제외" → false
			            - "주민등록번호 필수" -> false
			            - 급여이체, 카드사용, 자동이체, 청약 등 → true (외국인도 가능)
			            - "외국인등록번호", "거주외국인" → true
			            - 특정 금액 이상 예치 -> true(외국인도 가능)
			            - 애매한 경우 → true (보수적 판단 아님, 관대하게 판단)  
			            - 금융상품의 가입조건도 확인해서 너가 알아서 판단해서 가입할 수 있으면 true, 가입할 수 없으면 false로 반환해
			            
			다음 가입 조건이 외국인이 가입할 수 있는 가능성이 조금이라도 있다면 true 아니면 false를 반환한다
			
			다음은 금융상품의 전체 정보이다.
			-------------------------------------
			%s
			-------------------------------------
			반드시 소문자로 "true" 또는 "false"만 단독으로 출력하시오.
			""".formatted(conditionText);
	}
	private String callGpt(String prompt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		Map<String, Object> body = new HashMap<>();
		body.put("model", "gpt-4o");
		body.put("temperature", 0.7);
		body.put("max_tokens", 10);

		body.put("messages", new Object[] {
			Map.of("role", "system", "content", "너는 외국인이 가입 가능한지 판단하는 검증자야"),
			Map.of("role", "user", "content", prompt)
		});

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(gptApiUrl, request, Map.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			Map<String, Object> choices = ((Map<String, Object>)((java.util.List<?>)response.getBody()
				.get("choices")).get(0));
			String content = ((Map<String, String>)choices.get("message")).get("content").trim();
			return content.toLowerCase();
		} else {
			throw new RuntimeException("GPT API 호출 실패: " + response.getStatusCode());
		}
	}

	private boolean parseBooleanResponse(String response) {
		response = response.trim().toLowerCase();

		if (response.equals("true"))
			return true;
		if (response.equals("false"))
			return false;

		if (response.contains("true") && !response.contains("false"))
			return true;
		if (response.contains("false") && !response.contains("true"))
			return false;
		return false;
	}

	private String getProductName(Map<String, Object> productData) {
		Map<String, Object> result = (Map<String, Object>)productData.get("result");
		return (String)result.get("name");
	}
}
