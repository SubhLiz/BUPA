package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPTelephoneDto {

    private Integer telephoneId;
    private String requestId;
    private String country;
    private String countryCode;
    private String extension;
    private String telephone;
    private boolean standardNumber;
    private boolean smsEnabled;
    private boolean doNotUse;
    private String comments;
    private String telephoneCode;
    private String mobilePhone;
    private Boolean isNew;

}
