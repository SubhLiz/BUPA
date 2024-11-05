package com.incture.bupa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BPTaskBenchDataDto {

    private String requestId;
    private Integer requestTypeId;
    private String bupaNo;
    private String name1;
    private String createdBy;
    private String createdOn;
    private Integer statusId;
    private String systemId;
    private String subProcessType;
    private String bupaAccountGrp;
//    private BPAddressInfoDto bpAddressInfo;
    private String countryCode;
//    private ArrayList<BPCompanyCodeInfoDto>bpCompanyCodeInfo;
    private String companyCode;

}
