package org.scoula.domain.location.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;

public record Point(Double longitude,
					Double latitude) {

	@JsonIgnore
	public String toWKT() {
		return String.format("POINT(%f %f)", longitude, latitude);
	}

	public static Point fromWKT(String wkt) {
		String coordinates = wkt.replace("POINT(", "").replace(")", "");
		String[] parts = coordinates.split(" ");
		return new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
	}

}
