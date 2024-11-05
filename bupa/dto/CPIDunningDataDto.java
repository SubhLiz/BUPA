package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIDunningDataDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("CompanyCode")
    public String companyCode;
    @JsonProperty("DunningArea")
    public String dunningArea;
    @JsonProperty("DunnProcedure")
    public String dunnProcedure;
    @JsonProperty("DunnBlock")
    public String dunnBlock;
    @JsonProperty("LastDunned")
    public Object lastDunned;
    @JsonProperty("DunningLevel")
    public String dunningLevel;
    @JsonProperty("Dunnrecipient")
    public String dunnrecipient;
    @JsonProperty("Legdunnproc")
    public Object legdunnproc;
    @JsonProperty("Dunningclerk")
    public String dunningclerk;

}
