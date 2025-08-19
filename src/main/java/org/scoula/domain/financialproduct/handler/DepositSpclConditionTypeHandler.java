package org.scoula.domain.financialproduct.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;

@MappedTypes(DepositSpclConditionType.class)
public class DepositSpclConditionTypeHandler extends BaseTypeHandler<DepositSpclConditionType> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
		DepositSpclConditionType parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.name()); 
	}

	@Override
	public DepositSpclConditionType getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String value = rs.getString(columnName);
		return DepositSpclConditionType.fromEnglishName(value); 
	}

	@Override
	public DepositSpclConditionType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String value = rs.getString(columnIndex);
		return DepositSpclConditionType.fromEnglishName(value);
	}

	@Override
	public DepositSpclConditionType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String value = cs.getString(columnIndex);
		return DepositSpclConditionType.fromEnglishName(value);
	}
}
