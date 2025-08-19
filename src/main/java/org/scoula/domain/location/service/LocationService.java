package org.scoula.domain.location.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.location.dto.response.LocationResponse;
import org.scoula.domain.location.entity.Location;
import org.scoula.domain.location.entity.LocationType;
import org.scoula.domain.location.mapper.LocationMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LocationService {

	private final LocationMapper locationMapper;

	public List<LocationResponse> getAllCenters(HttpServletRequest request) {
		return locationMapper.selectAllCenters()
			.stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}

	private LocationResponse toResponse(Location location) {
		return LocationResponse.builder()
			.locationName(location.getLocationName())
			.address(location.getAddress())
			.point(location.getPoint())
			.tel(location.getTel())
			.homepageUrl(location.getHomepageUrl())
			.locationType(location.getLocationType())
			.build();
	}

	public List<LocationResponse> getAllBanks(HttpServletRequest request) {
		return locationMapper.selectAllBanks()
			.stream()
			.filter(location -> location.getLocationType() == LocationType.BANK)
			.map(this::toResponse)
			.collect(Collectors.toList());
	}
}
