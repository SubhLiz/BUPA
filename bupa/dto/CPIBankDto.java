package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIBankDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("Banknumber")
    public String banknumber;
    @JsonProperty("BankCountry")
    public String bankCountry;
    @JsonProperty("BankAccount")
    public String bankAccount;
    @JsonProperty("Controlkey")
    public String controlkey;
    @JsonProperty("PartBankType")
    public String partBankType;
    @JsonProperty("Collectauthor")
    public boolean collectauthor;
    @JsonProperty("Reference")
    public String reference;
    @JsonProperty("Accountholder")
    public String accountholder;
    @JsonProperty("IBAN")
    public String iBAN;
    @JsonProperty("IBANvalidfrom")
    public Object iBANvalidfrom;
    @JsonProperty("SwiftCode")
    public String swiftCode;

}
