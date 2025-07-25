package org.scoula.domain.location.entity;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PointTypeHandler extends BaseTypeHandler<Point> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Point parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.toWKT());
	}

	@Override
	public Point getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String wkt = rs.getString(columnName);
		return Point.fromWKT(wkt);
	}

	@Override
	public Point getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String wkt = rs.getString(columnIndex);
		return Point.fromWKT(wkt);
	}

	@Override
	public Point getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String wkt = cs.getString(columnIndex);
		return Point.fromWKT(wkt);
	}
}
