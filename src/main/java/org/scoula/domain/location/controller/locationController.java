package org.scoula.domain.location.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.location.dto.response.LocationResponse;
import org.scoula.domain.location.service.CsvDataProcessingService;
import org.scoula.domain.location.service.LocationService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "외국인 센터/은행 맵 ", description = "외국인 특화 센터와 은행 정보를 조회하는 API")
public class locationController {

	private final LocationService locationService;

	private final CsvDataProcessingService csvProcessingService;

	@ApiOperation(value = "센터 가져오기", notes = "외국인 특화 센터 목록을 가져옵니다.")
	@GetMapping("/centers")
	public SuccessResponse<List<LocationResponse>> getAllCentersList(HttpServletRequest request) {
		List<LocationResponse> centers = locationService.getAllCenters(request);
		return SuccessResponse.ok(centers);
	}

	@ApiOperation(value = "은행 가져오기", notes = "외국인 특화 은행 목록을 가져옵니다.")
	@GetMapping("/banks")
	public SuccessResponse<List<LocationResponse>> getAllBankList(HttpServletRequest request) {
		List<LocationResponse> banks = locationService.getAllBanks(request);
		return SuccessResponse.ok(banks);
	}

	@ApiOperation(value = "사용 X", notes = "내부 csv 파일로 저장용 API")
	@PostMapping("/import-foreign-centers")
	public SuccessResponse<String> importForeignCenters(HttpServletRequest request) {
		ClassLoader classLoader = getClass().getClassLoader();
		String csvPath;

		File file = new File(classLoader.getResource("foreign_center.csv").getFile());
		csvPath = file.getAbsolutePath();
		csvProcessingService.processForeignCenterCsv(request, csvPath);
		return SuccessResponse.ok("외국인 센터 데이터 임포트 완료");
	}

}
