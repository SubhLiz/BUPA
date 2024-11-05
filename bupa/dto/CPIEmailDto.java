package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIEmailDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("StandardNo")
    public boolean standardNo;
    @JsonProperty("EMailAddress")
    public String eMailAddress;
    @JsonProperty("EMailAddressSearch")
    public String eMailAddressSearch;
    @JsonProperty("Stdrecipient")
    public boolean stdrecipient;
    @JsonProperty("SAPConnection")
    public boolean sAPConnection;
    @JsonProperty("Coding")
    public String coding;
    @JsonProperty("TNEF")
    public boolean tNEF;
    @JsonProperty("ValidFrom")
    private String ValidFrom;
    @JsonProperty("ValidTo")
    private String ValidTo;
    @JsonProperty("Homeaddress")
    public boolean homeaddress;
    @JsonProperty("SequenceNumber")
    public String sequenceNumber;
    @JsonProperty("Error")
    public boolean error;
    @JsonProperty("Donotuse")
    public boolean donotuse;

}
