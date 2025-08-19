package org.scoula.domain.location.dto.response;

import lombok.Data;
import com.opencsv.bean.CsvBindByName;

@Data
public class ForeignCenterCsvDto {
    @CsvBindByName(column = "cnterNm")
    private String centerName;          
    
    @CsvBindByName(column = "roadNmAddr")
    private String roadAddress;         
    
    @CsvBindByName(column = "lat")
    private Double latitude;            
    
    @CsvBindByName(column = "lot")
    private Double longitude;            
    
    @CsvBindByName(column = "rprsTelno")
    private String representativeTelno;  
    
    @CsvBindByName(column = "dscsnTelno")
    private String consultTelno;        

    @CsvBindByName(column = "hmpgAddr")
    private String homepageUrl;          
}
