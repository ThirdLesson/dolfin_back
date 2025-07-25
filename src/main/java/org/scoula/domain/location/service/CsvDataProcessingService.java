package org.scoula.domain.location.service;

import org.scoula.domain.location.dto.response.ForeignCenterCsvDto;
import org.scoula.domain.location.entity.Location;
import org.scoula.domain.location.entity.LocationType;
import org.scoula.domain.location.entity.Point;
import org.scoula.domain.location.exception.LocationErrorCode;
import org.scoula.domain.location.mapper.LocationMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.security.exception.JwtErrorMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CsvDataProcessingService {

	private final LocationMapper locationMapper;

	public void processForeignCenterCsv(HttpServletRequest request, String csvFilePath) {
		try {
			// CSV 파일 읽기
			List<ForeignCenterCsvDto> csvData = new CsvToBeanBuilder<ForeignCenterCsvDto>(new FileReader(csvFilePath))
				.withType(ForeignCenterCsvDto.class)
				.withIgnoreLeadingWhiteSpace(true)
				.build()
				.parse();

			// DTO를 Location으로 변환
			List<Location> locations = csvData.stream()
				.map(this::convertToLocation)
				.filter(location -> !locationMapper.existsByLocationNumber(location.getLocationNumber()))
				.collect(Collectors.toList());

			// 배치로 저장 (성능 향상)
			if (!locations.isEmpty()) {
				locationMapper.insertLocationBatch(locations);
			}

		} catch (IOException e) {
			throw new CustomException(LocationErrorCode.JsonParse_API_FAILED,
				LogLevel.WARNING,
				request.getHeader("txId"),
				Common.builder()
					.srcIp(request.getRemoteAddr())
					.callApiPath(request.getRequestURI())
					.deviceInfo(request.getHeader("user-agent"))
					.retryCount(0)
					.build());
		}

	}

	private Location convertToLocation(ForeignCenterCsvDto dto) {
		// Point 객체 생성 (경도, 위도 순서)
		Point point = new Point(dto.getLongitude(), dto.getLatitude());

		// 전화번호 처리 (대표전화번호 우선, 없으면 상담전화번호 사용)
		String phoneNumber = dto.getRepresentativeTelno() != null ?
			dto.getRepresentativeTelno() : dto.getConsultTelno();

		return Location.builder()
			.locationName(dto.getCenterName())
			.address(dto.getRoadAddress())
			.point(point)
			.locationNumber(phoneNumber)
			.locationType(LocationType.CENTER)
			.build();
	}
}
