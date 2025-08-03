package org.scoula.domain.financialproduct.depositsaving.dto.common;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDetailResponse(PageProps pageProps
) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record PageProps(
		DehydratedState dehydratedState
	) {
		@JsonIgnoreProperties(ignoreUnknown = true)
		public record DehydratedState(
			List<Query> queries
		) {
			@JsonIgnoreProperties(ignoreUnknown = true)
			public record Query(
				State state,
				List<QueryKey> queryKey,
				String queryHash
			) {
				@JsonIgnoreProperties(ignoreUnknown = true)
				public record State(
					DetailData data
				) {
					@JsonIgnoreProperties(ignoreUnknown = true)
					public record DetailData(
						boolean isSuccess,
						String detailCode,
						String message,
						JsonNode result
					) {
						@JsonIgnoreProperties(ignoreUnknown = true)
						public record ProductDetail(
							String companyGroupCode,
							String typeCode,
							String partnerProductId,
							String name,
							String companyCode,
							String companyName,
							String partnerId,
							String partnerCode,
							boolean isBrokerage,
							List<String> features,
							List<String> specialConditions,
							List<String> productCategories,
							String interestRate,
							String primeInterestRate,
							Integer savingTerm,
							String specialOfferSummary,
							String specialOfferPeriodText,
							String joinPeriodText,
							String joinAmountText,
							String joinTarget,
							String channel,
							String savingOptionCode,
							String interestPaymentCycle,
							String note,
							String depositorProtectionText,
							String deliberationNumbers,
							Cu cu,
							Object cma,
							String updatedAt,
							String companyTelNo,
							String pcLinkUrl,
							String mobileLinkUrl,
							boolean isDirectSignUp
						) {
							@JsonIgnoreProperties(ignoreUnknown = true)
							public record Cu(
								String cuName,
								String address,
								String telNo
							) {
							}
						}
					}
				}
				@JsonIgnoreProperties(ignoreUnknown = true)
				public record QueryKey(
					String url,
					Map<String, String> params
				) {
				}
			}
		}
	}
}
