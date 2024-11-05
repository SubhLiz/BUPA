package com.incture.bupa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BPAdvanceRequestSearchCriteriaDto {
	private String createdBy;
    private String name1;
    private String bupaNo;
    private String requestId;
    private Integer requestTypeId;
    private Integer statusId;
    private String systemId;
    private String createdOn;
    private String searchTerm1;
    private String searchTerm2;
    private String district;
    private String region;
    private String email;
    private String telephone;
    private String contactPerson;
    private String bankAccount;
    private String iban;
    private String purchasingOrganization;
    private String companyCode;
    private String country;
    private String bupaAccountGrp;
    private Map<String,String>searchType;
    private int page;
    private int size;

}
