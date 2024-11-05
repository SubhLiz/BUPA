package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIContactPhoneDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("Addrnumber")
    public String addrnumber;
    @JsonProperty("Country")
    public String country;
    @JsonProperty("StdNo")
    public boolean stdNo;
    @JsonProperty("Telephone")
    public String telephone;
    @JsonProperty("Extension")
    public String extension;
    @JsonProperty("TelNo")
    public String telNo;
    @JsonProperty("CallerNo")
    public String callerNo;
    @JsonProperty("StdRecip")
    public String stdRecip;
    @JsonProperty("R3User")
    public String r3User;

}
