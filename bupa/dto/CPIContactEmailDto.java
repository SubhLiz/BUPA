package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIContactEmailDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("Addrnumber")
    public String addrnumber;
    @JsonProperty("StdndardNo")
    public boolean stdndardNo;
    @JsonProperty("EMail")
    public String eMail;
    @JsonProperty("EmailSrch")
    public String emailSrch;

}
