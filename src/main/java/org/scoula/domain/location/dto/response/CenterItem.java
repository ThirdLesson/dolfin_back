package org.scoula.domain.location.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CenterItem {
    private String cnterNm;        
    private String cnterChNm;        
    private String operMbyCn;        
    private String operModeCn;       
    private String ctpvNm;          
    private String sggNm;           
    private String roadNmAddr;       
    private String lotnoAddr;        
    private double lat;              
    private double lot;             
    private String hmpgAddr;         
    private String rprsTelno;        
    private String dscsnTelno;       
    private String fxno;            
    private String emlAddr;          
    private String operHrCn;        
    private int empCnt;            
    private String pvsnLngNm;       
    private String crtrYmd;        
    private String expsrYn;          
    private String rmrkCn;           
}
