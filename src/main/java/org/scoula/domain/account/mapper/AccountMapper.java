package org.scoula.domain.account.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.scoula.domain.account.dto.request.CreateAccountRequest;
import org.scoula.domain.account.entity.Account;

@Mapper
public interface AccountMapper {

	@Insert("""
		    INSERT INTO account (
		        wallet_id,
		        member_id,
		        account_number,
		        password,
		        balance,
		        bank_type,
		        is_verified,
		        verified_at
		    ) VALUES (
		        #{walletId},
		        #{memberId},
		        #{accountNumber},
		        '',                        
		        0,                        
		        #{bankType},
		        true,                    
		        NOW()
		    )
		""")
	void createAccount(CreateAccountRequest request);

	@Select("""
		    SELECT 
		        account_id,
		        wallet_id,
		        member_id,
		        account_number,
		        password,
		        balance,
		        bank_type,
		        is_verified,
		        verified_at,
		        created_at,
		        updated_at
		    FROM account
		    WHERE account_number = #{accountNumber}
		""")
	Account findByAccountNumber(String accountNumber);

	@Select("""
		    SELECT 
		        account_id,
		        wallet_id,
		        member_id,
		        account_number,
		        password,
		        balance,
		        bank_type,
		        is_verified,
		        verified_at,
		        created_at,
		        updated_at
		    FROM account
		    WHERE wallet_id = #{walletId}
		""")
	List<Account> findByWalletId(Long walletId);
}
