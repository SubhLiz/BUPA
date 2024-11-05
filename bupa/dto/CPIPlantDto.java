package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor
@NoArgsConstructor
public class CPIPlantDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("PurchasingOrg")
    public String purchasingOrg;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("VendorSubrange")
    public String vendorSubrange;
    @JsonProperty("Plant")
    public String plant;
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
    @JsonProperty("MRPController")
    public String mRPController;
    @JsonProperty("ConfControl")
    public String confControl;
    @JsonProperty("RndingProfile")
    public String rndingProfile;
    @JsonProperty("UoMGroup")
    public String uoMGroup;
    @JsonProperty("LBprofile")
    public String lBprofile;
    @JsonProperty("AutGRSetRet")
    public boolean autGRSetRet;
    @JsonProperty("PROACTcontrolprof")
    public String pROACTcontrolprof;
    @JsonProperty("Revaluation")
    public boolean revaluation;
    @JsonProperty("SrvBasedInvVer")
    public boolean srvBasedInvVer;

}
