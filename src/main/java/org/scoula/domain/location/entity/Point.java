package org.scoula.domain.location.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

// 내부 클래스로 Point 정의
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Point {
	private Double longitude;   // 경도 (x)
	private Double latitude;    // 위도 (y)

	// WKT(Well-Known Text) 형식으로 변환
	public String toWKT() {
		return String.format("POINT(%f %f)", longitude, latitude);
	}

	// WKT에서 Point 객체로 변환
	public static Point fromWKT(String wkt) {
		String coordinates = wkt.replace("POINT(", "").replace(")", "");
		String[] parts = coordinates.split(" ");
		return new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
	}

}
