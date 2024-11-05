package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPFaxInfoDto {

    private Integer faxId;
    private String requestId;
    private String country;
    private String countryCode;
    private String extension;
    private String fax;
    private boolean standardNumber;
    private boolean smsEnabled;
    private boolean doNotUse;
    private boolean notes;
    private String id;

}
