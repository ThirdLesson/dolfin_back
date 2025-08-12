package org.scoula.domain.remittancegroup.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.scoula.domain.remittancegroup.dto.request.JoinRemittanceGroupRequest;
import org.scoula.domain.remittancegroup.dto.request.RemittanceGroupMemberCountRequest;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupCheckResponse;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupCommissionResponse;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupMemberCountResponse;
import org.scoula.domain.remittancegroup.service.RemittanceService;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/remittance/group")
@Api(tags = "단체 송금 섹션", description = "단체 송금 섹션에 필요한 api")
public class RemittanceGroupController {

	private final RemittanceService remittanceService;

	@GetMapping("/commission")
	@ApiOperation(value = "수수료 조회", notes = "기존 수수료와 예상 수수료, 수수료의 차이를 조회하는 api.")
	public SuccessResponse<RemittanceGroupCommissionResponse> getCommission(HttpServletRequest request) {
		return SuccessResponse.ok(remittanceService.getRemittanceGroupCommission(request));
	}

	@PostMapping
	@ApiOperation(value = "단체 송금 상품 가입", notes = "단체 송금 상품에 가입하는 api.")
	public SuccessResponse<Void> joinRemittanceGroup(HttpServletRequest request,
		@RequestBody @Valid JoinRemittanceGroupRequest joinRemittanceGroupRequest,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		remittanceService.joinRemittanceGroup(joinRemittanceGroupRequest, customUserDetails.getMember(),
			request);

		return SuccessResponse.noContent();
	}

	@PostMapping("/count")
	@ApiOperation(value = "단체 송금 상품 날짜별 인원 수 조회", notes = "단체 송금 상품 날짜 별 가입 현황 상태 조회 api.")
	public SuccessResponse<List<RemittanceGroupMemberCountResponse>> getRemittanceGroupCount(
		@RequestBody @Valid RemittanceGroupMemberCountRequest remittanceGroupMemberCountRequest,
		HttpServletRequest request) {
		return SuccessResponse.ok(
			remittanceService.getRemittanceGroupMemberCount(remittanceGroupMemberCountRequest.currency(), request));
	}

	@GetMapping("/check/info")
	@ApiOperation(value = "단체 송금 상품 가입 정보 확인", notes = "단체 송금 상품에 가입한 사용자 정보를 확인하는 api.")
	public SuccessResponse<RemittanceGroupCheckResponse> checkRemittanceGroupExist(
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return SuccessResponse.ok(
			remittanceService.checkRemittanceGroupExist(customUserDetails.getMember())
		);
	}

	@DeleteMapping("/cancel")
	@ApiOperation(value = "단체 송금 상품 해지", notes = "단체 송금 상품을 해지하는 api.")
	public SuccessResponse<Void> cancelRemittanceGroup(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		HttpServletRequest request) {
		remittanceService.cancelRemittanceGroup(customUserDetails.getMember(),request);
		return SuccessResponse.noContent();
	}


}
