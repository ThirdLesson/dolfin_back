package org.scoula.domain.remmitanceinformation.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.scoula.domain.remmitanceinformation.entity.RemittanceInformation;

@Mapper
public interface RemittanceInformationMapper {

	@Insert("""
		<script>
		INSERT INTO remittance_information
		<trim prefix="(" suffix=")" suffixOverrides=",">
		    receiver_bank,
		    swift_code,
		    <if test="routerCode != null">router_code,</if>
		    receiver_account,
		    receiver_name,
		    receiver_nationality,
		    receiver_address,
		    purpose,
		    amount,
		    <if test="transmitFailCount != null">transmit_fail_count,</if>
		    <if test="intermediaryBankCommission != null">intermediary_bank_commission,</if>
		    created_at,
		    updated_at
		</trim>
		<trim prefix="VALUES (" suffix=")" suffixOverrides=",">
		    #{receiverBank},
		    #{swiftCode},
		    <if test="routerCode != null">#{routerCode},</if>
		    #{receiverAccount},
		    #{receiverName},
		    #{receiverNationality},
		    #{receiverAddress},
		    #{purpose},
		    #{amount},
		    <if test="transmitFailCount != null">#{transmitFailCount},</if>
		    <if test="intermediaryBankCommission != null">#{intermediaryBankCommission},</if>
		    CURRENT_TIMESTAMP,
		    CURRENT_TIMESTAMP
		</trim>
		</script>
		""")
	@Options(useGeneratedKeys = true,
		keyProperty = "remittanceInformationId",
		keyColumn = "remittance_information_id")
	int insert(RemittanceInformation info);
}
