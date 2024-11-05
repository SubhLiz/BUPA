package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIFaxDto {

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
    @JsonProperty("FAX")
    public String fax;
    @JsonProperty("Extension")
    public String extension;
    @JsonProperty("Faxnumber")
    public String faxnumber;
    @JsonProperty("Sendernumber")
    public String sendernumber;
    @JsonProperty("Faxgroup")
    public String faxgroup;
    @JsonProperty("Stdrecipient")
    public boolean stdrecipient;
    @JsonProperty("SAPConnection")
    public boolean sAPConnection;
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
