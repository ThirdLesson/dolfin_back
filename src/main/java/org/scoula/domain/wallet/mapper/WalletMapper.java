package org.scoula.domain.wallet.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.scoula.domain.codef.dto.request.WalletRequest;
import org.scoula.domain.wallet.entity.Wallet;

@Mapper
public interface WalletMapper {

	@Insert("""
		    INSERT INTO wallet (balance, password, member_id)
		    VALUES (0, #{wallet.walletPassword}, #{memberId})
		""")
	@Options(useGeneratedKeys = true, keyProperty = "wallet.walletId")
	void createWallet(@Param("wallet") WalletRequest wallet, @Param("memberId") Long memberId);

	@Select("""
		    SELECT wallet_id, balance, password, member_id, created_at, updated_at
		    FROM wallet
		    WHERE member_id = #{memberId}
		""")
	Wallet findByMemberId(@Param("memberId") Long memberId);
}
