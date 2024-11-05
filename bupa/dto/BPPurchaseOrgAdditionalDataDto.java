package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPPurchaseOrgAdditionalDataDto {
	private Integer purchaseOrgAddDataId;
	private String purchasingOrg;
	private String plant;
	private String orderCurrency;
	private String termsOfPayment;
	private String incoTerms;
	private String incoTerms2;
	private String minOrderValue;
	private String pricingDateControl;
	private String orderOptimRest;
	private String abcIndicator;
	private String modeofTransportBorder;
	private String officeOfEntry;
	private String proActControlProf;
	private Boolean extend;
	private Boolean grBasedInvVerify;
	private Boolean autoEvalGRSetMtDel;
	private Boolean autoEvalGRSetMtRet;
	private Boolean acknowledgementReqd;
	private Boolean automaticPurchaseOrder;
	private Boolean srvBasedInvVar;
	private Boolean revaluation;
	private String purchasingGroup;
	private String mrpController;
	private String planneddelivtime;
	private String deliveryCycle;
	private String planningCycle;
	private String confirmationControl;
	private String unitofMeasureGroup;
	private String roundingProfile;
	private String creationProfile;
	private String vendorPriceMarking;
	private Boolean rackJobbingServiceAgreed;
	private Boolean orderEntryByVendor;
}
