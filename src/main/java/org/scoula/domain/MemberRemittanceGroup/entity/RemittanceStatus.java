package org.scoula.domain.MemberRemittanceGroup.entity;

import lombok.Getter;

@Getter
public enum RemittanceStatus {
        PENDING("진행중"),
        SUCCESS("성공"), 
        FAILED("실패");
        
        private final String description;
        
        RemittanceStatus(String description) {
            this.description = description;
        }

    }
