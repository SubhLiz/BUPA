package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIPhoneDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("Country")
    public String country;
    @JsonProperty("ISOcode")
    public String iSOcode;
    @JsonProperty("StandardNo")
    public boolean standardNo;
    @JsonProperty("Telephone")
    public String telephone;
    @JsonProperty("Extension")
    public String extension;
    @JsonProperty("Telephoneno")
    public String telephoneno;
    @JsonProperty("Callernumber")
    public String callernumber;
    @JsonProperty("SMSEnab")
    public String sMSEnab;
    @JsonProperty("Mobilephone")
    public String mobilephone;
    @JsonProperty("Homeaddress")
    public boolean homeaddress;
    @JsonProperty("SequenceNumber")
    public String sequenceNumber;
    @JsonProperty("Error")
    public boolean error;
    @JsonProperty("Donotuse")
    public boolean donotuse;
    @JsonProperty("ValidFrom")
    private String ValidFrom;
    @JsonProperty("ValidTo")
    private String ValidTo;

}
