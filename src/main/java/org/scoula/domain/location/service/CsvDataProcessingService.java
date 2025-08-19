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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@RequiredArgsConstructor
public class CsvDataProcessingService {

	private final LocationMapper locationMapper;

	@Transactional
	public void processForeignCenterCsv(HttpServletRequest request, String csvFilePath) {
		try {
			List<ForeignCenterCsvDto> csvData = new CsvToBeanBuilder<ForeignCenterCsvDto>(new FileReader(csvFilePath))
				.withType(ForeignCenterCsvDto.class)
				.withIgnoreLeadingWhiteSpace(true)
				.build()
				.parse();

			List<Location> locations = csvData.stream()
				.map(this::convertToLocation)
				.collect(Collectors.toList());

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
		Point point = new Point(dto.getLongitude(), dto.getLatitude());


		String raw = dto.getRepresentativeTelno();
		StringBuilder phoneNumberBuilder = new StringBuilder(raw);

		if (raw.startsWith("02")) {
			if (raw.length() == 9) { 
				phoneNumberBuilder.insert(2, "-");
				phoneNumberBuilder.insert(6, "-");
			} else if (raw.length() == 10) { 
				phoneNumberBuilder.insert(2, "-");
				phoneNumberBuilder.insert(7, "-");
			}
		} else {
			if (phoneNumberBuilder.length() == 8) {
				phoneNumberBuilder.insert(0, "0");
				phoneNumberBuilder.insert(3, "-");
				phoneNumberBuilder.insert(7, "-");
			} else if (phoneNumberBuilder.length() == 9) {
				phoneNumberBuilder.insert(0, "0");
				phoneNumberBuilder.insert(3, "-");
				phoneNumberBuilder.insert(7, "-");
			} else if (phoneNumberBuilder.length() == 10) {
				phoneNumberBuilder.insert(3, "-");
				phoneNumberBuilder.insert(7, "-");
			} else if (phoneNumberBuilder.length() == 11) {
				phoneNumberBuilder.insert(3, "-");
				phoneNumberBuilder.insert(8, "-");
			}
		}
		return Location.builder()
			.locationName(dto.getCenterName())
			.address(dto.getRoadAddress())
			.point(point)
			.tel(phoneNumberBuilder.toString())
			.homepageUrl(dto.getHomepageUrl())
			.locationType(LocationType.CENTER)
			.build();
	}
}
