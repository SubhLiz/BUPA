package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPICompanyDataDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("CompanyCode")
    public String companyCode;
    @JsonProperty("Cocodepostblock")
    public boolean cocodepostblock;
    @JsonProperty("Cocdedeletionflag")
    public boolean cocdedeletionflag;
    @JsonProperty("Sortkey")
    public String sortkey;
    @JsonProperty("Reconaccount")
    public String reconaccount;
    @JsonProperty("Authorization")
    public String authorization;
    @JsonProperty("Interestindic")
    public String interestindic;
    @JsonProperty("Paymentmethods")
    public String paymentmethods;
    @JsonProperty("Clrgwithcust")
    public boolean clrgwithcust;
    @JsonProperty("Paymentblock")
    public String paymentblock;
    @JsonProperty("PaytTerms")
    public String paytTerms;
    @JsonProperty("Acctvendor")
    public String acctvendor;
    @JsonProperty("Clerkatvendor")
    public String clerkatvendor;
    @JsonProperty("Accountmemo")
    public String accountmemo;
    @JsonProperty("Planninggroup")
    public String planninggroup;
    @JsonProperty("Acctgclerk")
    public String acctgclerk;
    @JsonProperty("Headoffice")
    public String headoffice;
    @JsonProperty("Alternatpayee")
    public String alternatpayee;
    @JsonProperty("Lastkeydate")
    public Object lastkeydate;
    @JsonProperty("Intcalcfreq")
    public String intcalcfreq;
    @JsonProperty("Lastintcalc")
    public Object lastintcalc;
    @JsonProperty("Localprocess")
    public boolean localprocess;
    @JsonProperty("Bexchlimit")
    public String bexchlimit;
    @JsonProperty("Chkcashngtime")
    public String chkcashngtime;
    @JsonProperty("Chkdoubleinv")
    public boolean chkdoubleinv;
    @JsonProperty("Tolerancegroup")
    public String tolerancegroup;
    @JsonProperty("HouseBank")
    public String houseBank;
    @JsonProperty("Individualpmnt")
    public boolean individualpmnt;
    @JsonProperty("Exemptionno")
    public String exemptionno;
    @JsonProperty("Validuntil")
    public Object validuntil;
    @JsonProperty("WTaxCode")
    public String wTaxCode;
    @JsonProperty("Subsind")
    public String subsind;
    @JsonProperty("maineconomicact")
    private String maineconomicact;
    @JsonProperty("Minorityindic")
    public String minorityindic;
    @JsonProperty("Prevacctno")
    public String prevacctno;
    @JsonProperty("Groupingkey1")
    public String groupingkey1;
    @JsonProperty("Groupingkey2")
    public String groupingkey2;
    @JsonProperty("Pmtmethsupl")
    public String pmtmethsupl;
    @JsonProperty("Recipienttype")
    public String recipienttype;
    @JsonProperty("Exmptauthority")
    public String exmptauthority;
    @JsonProperty("CountryForWT")
    public String countryForWT;
    @JsonProperty("PmtadvbyEDI")
    public boolean pmtadvbyEDI;
    @JsonProperty("Releasegroup")
    public String releasegroup;
    @JsonProperty("Clerksfax")
    public String clerksfax;
    @JsonProperty("Clrksinternet")
    public String clrksinternet;
    @JsonProperty("Crmemoterms")
    public String crmemoterms;
    @JsonProperty("ActivityCode")
    public String activityCode;
    @JsonProperty("DistrType")
    public String distrType;
    @JsonProperty("Acctstatement")
    public String acctstatement;
    @JsonProperty("CertifictnDate")
    public Object certifictnDate;
    @JsonProperty("Tolerancegrp")
    public String tolerancegrp;
    @JsonProperty("PersonnelNo")
    public String personnelNo;
    @JsonProperty("CoCddelblock")
    public boolean coCddelblock;
    @JsonProperty("Actclktelno")
    public String actclktelno;
    @JsonProperty("PrepaymentRelevant")
    public String prepaymentRelevant;
    @JsonProperty("AssignmTestGroup")
    public String assignmTestGroup;
    @JsonProperty("PurposeCompleteFlag")
    public String purposeCompleteFlag;
    @JsonProperty("Branchcode")
    public String branchCode;
    @JsonProperty("Branchcodedesc")
    public String branchCodeDescription;
    @JsonProperty("ToDunningData")
    public List<CPIDunningDataDto> toDunningData;
    @JsonProperty("ToWtax")
    public List<CPIwTaxDto> toWtax;

}
