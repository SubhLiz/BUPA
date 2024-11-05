package com.incture.bupa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BPRequestSearchCriteriaDto {

    private String requestId;
    private Integer requestTypeId;
    private Integer statusId;
    private String name1;
    private String createdBy;
    private String bupaNo;
//    private Date createdOn;
    private String createdOn;
    private String searchTerm1;
    private String searchTerm2;
    private String district;
    private String region;
    private String email;
    private String telephone;
    private String contactPerson;
    private String bankAccountNo;
    private String iban;
    private String purchasingOrg;
    private String companyCode;
    private String systemId;
    private int page;
    private int size;

}
