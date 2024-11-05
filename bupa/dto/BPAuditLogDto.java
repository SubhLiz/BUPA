package com.incture.bupa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BPAuditLogDto {
    private UUID auditLogInfoId;
    private String requestId;
    private String fieldName;
    private String uiFieldName;
    private String oldValue;
    private String newValue;
    private String companyCode;
    private String flag;
    private String purchasingOrg;
    private String plant;
    private String updatedOn;
    private String path;
    private String updatedBy;
    private Integer serialNo;
}
