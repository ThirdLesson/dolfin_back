// package org.scoula.domain.financialproduct.helper;
//
// import org.scoula.domain.financialproduct.jeonseloan.mapper.JeonseLoanMapper;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class JeonseLoanApiHelper {
//
// 	private  final RestTemplate restTemplate;
//
// 	@Value("${fss.api.jeonse-loan_url}")
// 	private String apiBaseUrl;
//
// 	@Value("${fss.api.key}")
// 	private String apiKey;
//
// 	public JeonseLoanApiResponse callExternalApi(){
// 		String url = builApiurl();
// 		log.info("API 호출 URL : {}",url);
// 		return restTemplate.getForObject(url, JeonseLoanMapper.class);
// 	}
//
// 	public void validateApiResponse(JeoseLoanApiResponse response){
//
// 	}
//
// }
