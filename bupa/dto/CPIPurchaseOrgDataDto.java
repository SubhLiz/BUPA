package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIPurchaseOrgDataDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("Allvendor")
    public String allvendor;
    @JsonProperty("PurchasingOrg")
    public String purchasingOrg;
    @JsonProperty("PurblockPOrg")
    public boolean purblockPOrg;
    @JsonProperty("DelflagPOrg")
    public boolean delflagPOrg;
    @JsonProperty("ABCindicator")
    public String aBCindicator;
    @JsonProperty("Ordercurrency")
    public String ordercurrency;
    @JsonProperty("Salesperson")
    public String salesperson;
    @JsonProperty("Telephone")
    public String telephone;
    @JsonProperty("Minimumvalue")
    public String minimumvalue;
    @JsonProperty("PaytTerms")
    public String paytTerms;
    @JsonProperty("Incoterms")
    public String incoterms;
    @JsonProperty("Incoterms2")
    public String incoterms2;
    @JsonProperty("GRBasedIV")
    public boolean gRBasedIV;
    @JsonProperty("AcknowlReqd")
    public boolean acknowlReqd;
    @JsonProperty("SchemaGrpVndr")
    public String schemaGrpVndr;
    @JsonProperty("AutomaticPO")
    public boolean automaticPO;
    @JsonProperty("ModeOfTrBorder")
    public String modeOfTrBorder;
    @JsonProperty("Customsoffice")
    public String customsoffice;
    @JsonProperty("PrDateCat")
    public String prDateCat;
    @JsonProperty("PurchGroup")
    public String purchGroup;
    @JsonProperty("Subseqsett")
    public boolean subseqsett;
    @JsonProperty("Bvolcompag")
    public boolean bvolcompag;
    @JsonProperty("ERS")
    public boolean eRS;
    @JsonProperty("PlDelivTime")
    public String plDelivTime;
    @JsonProperty("Planningcal")
    public String planningcal;
    @JsonProperty("Planningcycle")
    public String planningcycle;
    @JsonProperty("POentryvend")
    public String pOentryvend;
    @JsonProperty("Pricemkgvnd")
    public String pricemkgvnd;
    @JsonProperty("Rackjobbing")
    public String rackjobbing;
    @JsonProperty("SSindexactive")
    public boolean sSindexactive;
    @JsonProperty("Pricedetermin")
    public boolean pricedetermin;
    @JsonProperty("QualiffDKd")
    public String qualiffDKd;
    @JsonProperty("DocumentIndex")
    public boolean documentIndex;
    @JsonProperty("Sortcriterion")
    public String sortcriterion;
    @JsonProperty("ConfControl")
    public String confControl;
    @JsonProperty("RndingProfile")
    public String rndingProfile;
    @JsonProperty("UoMGroup")
    public String uoMGroup;
    @JsonProperty("VenServLevl")
    public String venServLevl;
    @JsonProperty("LBprofile")
    public String lBprofile;
    @JsonProperty("AutGRSetRet")
    public boolean autGRSetRet;
    @JsonProperty("Accwvendor")
    public String accwvendor;
    @JsonProperty("PROACTcontrolprof")
    public String pROACTcontrolprof;
    @JsonProperty("Agencybusiness")
    public boolean agencybusiness;
    @JsonProperty("Revaluation")
    public boolean revaluation;
    @JsonProperty("ShippingCond")
    public String shippingCond;
    @JsonProperty("SrvBasedInvVer")
    public boolean srvBasedInvVer;
    @JsonProperty("ToPlant")
    public List<CPIPlantDto> toPlant;
    @JsonProperty("ToOderingAddress")
    public List<CPIOrderingAddressDto> toOderingAddress;
    @JsonProperty("ToInvoiceParty")
    public List<CPIInvoicePartyDto> toInvoiceParty;

}
