package org.scoula.domain.location.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// 개별 센터 정보
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CenterItem {
    private String cnterNm;          // 센터명
    private String cnterChNm;        // 센터장명
    private String operMbyCn;        // 운영주체
    private String operModeCn;       // 운영방식
    private String ctpvNm;           // 시도명
    private String sggNm;            // 시군구명
    private String roadNmAddr;       // 도로명주소
    private String lotnoAddr;        // 지번주소
    private double lat;              // 위도
    private double lot;              // 경도
    private String hmpgAddr;         // 홈페이지
    private String rprsTelno;        // 대표전화
    private String dscsnTelno;       // 상담전화
    private String fxno;             // 팩스번호
    private String emlAddr;          // 이메일
    private String operHrCn;         // 운영시간
    private int empCnt;              // 직원수
    private String pvsnLngNm;        // 제공언어
    private String crtrYmd;          // 생성일자
    private String expsrYn;          // 노출여부
    private String rmrkCn;           // 비고
}
