package org.scoula.domain.financialproduct.depositsaving.dto.common;

import java.util.List;
import java.util.Map;

public record ProductDetailResponse(PageProps pageProps
) {
	public record PageProps(
		DehydratedState dehydratedState
	) {
		public record DehydratedState(
			List<Query> queries
		) {
			public record Query(
				State state,
				List<QueryKey> queryKey
			) {
				public record State(
					DetailData data
				) {
					public record DetailData(
						boolean isSuccess,
						String detailCode,
						String message,
						ProductDetail result
					) {
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
							public record Cu(
								String cuName,
								String address,
								String telNo
							) {
							}
						}
					}
				}

				public record QueryKey(
					String url,
					Map<String, String> params
				) {
				}
			}
		}
	}
}
