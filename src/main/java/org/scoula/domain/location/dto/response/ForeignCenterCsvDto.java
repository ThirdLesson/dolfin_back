package org.scoula.domain.location.dto.response;

import lombok.Data;
import com.opencsv.bean.CsvBindByName;

@Data
public class ForeignCenterCsvDto {
    @CsvBindByName(column = "cnterNm")
    private String centerName;           // 센터명
    
    @CsvBindByName(column = "roadNmAddr")
    private String roadAddress;          // 도로명주소
    
    @CsvBindByName(column = "lat")
    private Double latitude;             // 위도
    
    @CsvBindByName(column = "lot")
    private Double longitude;            // 경도
    
    @CsvBindByName(column = "rprsTelno")
    private String representativeTelno;  // 대표전화번호
    
    @CsvBindByName(column = "dscsnTelno")
    private String consultTelno;         // 상담전화번호
}
