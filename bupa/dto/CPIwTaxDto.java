package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIwTaxDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("CompanyCode")
    public String companyCode;
    @JsonProperty("Country")
    public String country;
    @JsonProperty("Withhldtaxtype")
    public String withhldtaxtype;
    @JsonProperty("Subjecttowtx")
    public boolean subjecttowtx;
    @JsonProperty("Recipienttype")
    public String recipienttype;
    @JsonProperty("Wtaxnumber")
    public String wtaxnumber;
    @JsonProperty("Wtaxcode")
    public String wtaxcode;
    @JsonProperty("Exemptionnumber")
    public String exemptionnumber;
    @JsonProperty("Exemptionrate")
    public String exemptionrate;
    public String exemptfrom;
    @JsonProperty("ExemptTo")
    public String exemptTo;
    @JsonProperty("Exemptionreas")
    public String exemptionreas;

}
