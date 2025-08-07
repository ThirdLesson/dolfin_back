package org.scoula.domain.financialproduct.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;

@MappedTypes(SavingSpclConditionType.class)
public class SavingSpclConditionTypeHandler extends BaseTypeHandler<SavingSpclConditionType> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, SavingSpclConditionType parameter,
		JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.name());

	}

	@Override
	public SavingSpclConditionType getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String value = rs.getString(columnName);
		return SavingSpclConditionType.fromEnglishName(value); // 커스텀 매핑 로직 사용
	}

	@Override
	public SavingSpclConditionType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String value = rs.getString(columnIndex);
		return SavingSpclConditionType.fromEnglishName(value);
	}
	@Override
	public SavingSpclConditionType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String value = cs.getString(columnIndex);
		return SavingSpclConditionType.fromEnglishName(value);
	}
}


