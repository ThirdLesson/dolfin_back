package org.scoula.domain.remittancegroup.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.scoula.domain.remittancegroup.entity.BenefitStatus;
import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.scoula.global.constants.Currency;

@Mapper
public interface RemittanceGroupMapper {

	@Select("""
		SELECT
		    remittance_group_id,
		    benefit_status,
		    remittance_date,
		    member_count,
		    currency,
		    created_at,
		    updated_at
		FROM remittance_group
		WHERE currency = #{currency}
		  AND benefit_status = #{benefitStatus}
		  AND remittance_date = #{remittanceDate}
		LIMIT 1
		FOR UPDATE
		""")
	Optional<RemittanceGroup> findByCurrencyAndBenefitStatusAndRemittanceDate(
		@Param("currency") Currency currency,
		@Param("benefitStatus") BenefitStatus benefitStatus,
		@Param("remittanceDate") Integer remittanceDate
	);

	@Insert("""
		<script>
		INSERT INTO remittance_group
		<trim prefix="(" suffix=")" suffixOverrides=",">
		    benefit_status,
		    remittance_date,
		    <if test="memberCount != null">member_count,</if>
		    currency
		</trim>
		<trim prefix="VALUES (" suffix=")" suffixOverrides=",">
		    #{benefitStatus},
		    #{remittanceDate},
		    <if test="memberCount != null">#{memberCount},</if>
		    #{currency}
		</trim>
		</script>
		""")
	@Options(useGeneratedKeys = true,
		keyProperty = "remittanceGroupId",
		keyColumn = "remittance_group_id")
	int insert(RemittanceGroup group);

	@Update("""
		UPDATE remittance_group
		SET member_count = #{memberCount}
		WHERE remittance_group_id = #{remittanceGroupId}
		""")
	int updateMemberCountById(@Param("remittanceGroupId") Long remittanceGroupId,
		@Param("memberCount") Integer memberCount);

	@Update("""
		UPDATE remittance_group
		SET benefit_status = NULL
		WHERE remittance_group_id = #{remittanceGroupId}
		""")
	int updateBenefitStatusOnById(@Param("remittanceGroupId") Long remittanceGroupId);

	@Select("""
		SELECT
		    *
		FROM remittance_group
		WHERE currency = #{currency}
		  AND benefit_status = 'OFF'
		ORDER BY remittance_date ASC
		""")
	List<RemittanceGroup> findAllByCurrencyAndBenefitStatusOff(@Param("currency") Currency currency);

	@Select("""
		SELECT
		    MIN(remittance_group_id)
		FROM remittance_group
		WHERE benefit_status IS NULL
		""")
	Long findMinIdBenefitStatusOn();

	@Select("""
		SELECT
		    MAX(remittance_group_id)
		FROM remittance_group
		WHERE benefit_status IS NULL
		""")
	Long findMaxIdBenefitStatusOn();

	@Select("""
			SELECT *
			FROM remittance_group
			WHERE benefit_status IS NULL
		 		AND remittance_date = #{day}
			  AND remittance_group_id BETWEEN #{startId} AND #{endId}
			ORDER BY remittance_group_id
		""")
	List<RemittanceGroup> findByIdRange(@Param("startId") Long startId, @Param("endId") Long endId,
		@Param("day") Integer day);

	@Select("""
			SELECT *
			FROM remittance_group
			WHERE benefit_status IS NULL
		 		AND remittance_date = #{day}
		""")
	List<RemittanceGroup> findByDayBenefitOn(@Param("day") Integer day);

}
