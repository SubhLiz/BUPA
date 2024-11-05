package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPMobilePhoneDto {

    private Integer mobileId;
    private String requestId;
    private String country;
    private String countryCode;
    private String extension;
    private String telephone;
    private boolean standardNumber;
    private boolean smsEnabled;
    private boolean doNotUse;
    private String comments;
    private String mobileCode;
    private String mobilePhone;
    private Boolean isNew;

}
