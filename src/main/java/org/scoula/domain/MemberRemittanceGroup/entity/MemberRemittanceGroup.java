package org.scoula.domain.MemberRemittanceGroup.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRemittanceGroup {
    
    private Long memberRemittanceGroup;   // PK
    private Long remittanceGroupId;       // 단체송금 아이디 (FK)
    private Long memberId;                // 회원 아이디 (FK)
    private String receiverBank;          // 수취인 은행 이름
    private String swiftCode;             // SWIFT 코드
    private String routerCode;            // 라우터 코드(미국)
    private String receiverAccount;       // 수취인 계좌
    private String receiverName;          // 수취인 이름
    private String receiverNationality;   // 수취인 국가
    private String receiverAddress;       // 수취인 주소
    private String purpose;               // 거래목적
    private Long amount;                  // 환전을 원하는 금액(원화)
    private Long exchangeAmount;          // 환전이 완료된 금액
    private RemittanceStatus status;      // 진행(진행중, 성공, 실패)

}
