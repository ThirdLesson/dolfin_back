package org.scoula.domain.financialproduct.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.scoula.domain.financialproduct.depositsaving.dto.common.DepositProduct;
import org.scoula.domain.financialproduct.depositsaving.dto.common.ProductDetailResponse;
import org.scoula.domain.financialproduct.depositsaving.dto.common.ProductListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositApiHelper {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${deposit.product.list}")
	private String DEPOSIT_PRODUCT_LIST_BY_PERIOD_URL;
	@Value("${deposit.product.detail.url}")
	private String DEPOSIT_DETAIL_URL;

	// 기간별 상품 API 호출
	public List<DepositProduct> getProductsByPeriod(int period, int offset) throws JsonProcessingException {
		String url = String.format(DEPOSIT_PRODUCT_LIST_BY_PERIOD_URL, period, offset);
		String rawResponse = restTemplate.getForObject(url, String.class);

		if (rawResponse == null || rawResponse.isEmpty()) {
			return Collections.emptyList();
		}

		ProductListResponse<DepositProduct> response = parseProductListResponse(rawResponse);

		if (response == null || !response.isSuccess()) {
			return Collections.emptyList();
		}
		if (response.result() == null || response.result().products() == null) {
			return Collections.emptyList();
		}
		return response.result().products();
	}

	// 상품 상세 정보 API 호출
	public ProductDetailInfo getProductDetailInfo(String productCode) throws JsonProcessingException {
		String url = String.format(DEPOSIT_DETAIL_URL, productCode, productCode);
		String rawResponse = restTemplate.getForObject(url, String.class);

		if (rawResponse == null || rawResponse.isEmpty()) {
			return new ProductDetailInfo(Collections.emptyList(), Collections.emptyList());
		}
		return parseProductDetailResponse(rawResponse, productCode);
	}

	// 상품 리스트 응답 파싱
	private ProductListResponse<DepositProduct> parseProductListResponse(String rawResponse) throws
		JsonProcessingException {
		JavaType type = objectMapper.getTypeFactory()
			.constructParametricType(ProductListResponse.class, DepositProduct.class);

		return objectMapper.readValue(rawResponse, type);
	}

	// 상품 상세 응답 파싱
	private ProductDetailInfo parseProductDetailResponse(String rawResponse, String productCode) throws
		JsonProcessingException {
		ProductDetailResponse response = objectMapper.readValue(rawResponse, ProductDetailResponse.class);
		JsonNode result = extractProductDetailResult(response);

		if (result == null) {
			return new ProductDetailInfo(Collections.emptyList(), Collections.emptyList());
		}
		return parseSpecialConditions(result, productCode);
	}

	// 상품 상세 결과 추출
	private JsonNode extractProductDetailResult(ProductDetailResponse response) {
		return response.pageProps()
			.dehydratedState()
			.queries()
			.stream()
			.filter(query -> query.queryKey().stream()
				.anyMatch(key -> "/productDetails".equals(key.url())))
			.findFirst()
			.map(query -> query.state().data().result())
			.filter(result -> !result.isBoolean())
			.orElse(null);
	}

	// 우대조건 파싱
	private ProductDetailInfo parseSpecialConditions(JsonNode result, String productCode) {
		List<String> specialConditions = new ArrayList<>();

		if (result.has("specialConditions") && result.get("specialConditions").isArray()) {
			for (JsonNode cond : result.get("specialConditions")) {
				String conditionText = cond.asText();
				if (!conditionText.isEmpty()) {
					specialConditions.add(conditionText);
				}
			}
		} else if (result.has("spclConditions") && result.get("spclConditions").isArray()) {
			for (JsonNode cond : result.get("spclConditions")) {
				String conditionText = cond.asText();
				if (!conditionText.isEmpty()) {
					specialConditions.add(conditionText);
				}
			}
		}
		// 문자열 형태로 되어있을 수도 있음
		else if (result.has("specialConditions") && result.get("specialConditions").isTextual()) {
			String conditionText = result.get("specialConditions").asText();
			if (!conditionText.isEmpty()) {
				String[] conditions = conditionText.split("[,;|]");
				for (String condition : conditions) {
					String trimmed = condition.trim();
					if (!trimmed.isEmpty()) {
						specialConditions.add(trimmed);
					}
				}
			}
		}
		List<Integer> savingTerms = new ArrayList<>();
		if (result.has("savingTerm") && result.get("savingTerm").isInt()) {
			savingTerms.add(result.get("savingTerm").asInt());
		} else if (result.has("savingTerm") && result.get("savingTerm").isArray()) {
			for (JsonNode term : result.get("savingTerm")) {
				if (term.isInt()) {
					savingTerms.add(term.asInt());
				}
			}
		}else{
				savingTerms.add(12); // 기본값
			}
			return new ProductDetailInfo(specialConditions, savingTerms);
		}

		// 상품 상세 정보 record
		public record ProductDetailInfo(
			List<String> specialConditions,
			List<Integer> savingTerms
		) {}
	}

