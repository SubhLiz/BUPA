package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPPurchaseOrgDto {
	private Integer purchaseOrgId;
	private String purchasingOrg;
	private String orderCurrency;
	private String termsOfPayment;
	private String incoTerms;
	private String incoTerms2;
	private String minOrderValue;
	private String schemaGroupVendor;
	private String pricingDateControl;
	private String orderOptimRest;
	private String accWithVendor;
	private String abcIndicator;
	private String modeOfTransportBorder;
	private String officeOfEntry;
	private String sortCriterion;
	private String proActControlProf;
	private String shippingConditions;
	private Boolean grBasedInvVerify;
	private Boolean autoEvalGRSetMtDel;
	private Boolean autoEvalGRSetMtRet;
	private Boolean acknowledgementReqd;
	private Boolean automaticPurchaseOrder;
	private Boolean subsequentSettlement;
	private Boolean bVolComp;
	private Boolean docIndexActive;
	private Boolean returnsVendor;
	private Boolean srvBasedInvVar;
	private Boolean revaluationAllowed;
	private Boolean grantDiscountInKind;
	private Boolean relevantForPriceDet;
	private Boolean relevantForAgencyBusiness;
	private String purchasingGroup;
	private String plannedDelivTime;
	private String confirmationControl;
	private String unitOfMeasureGroup;
	private String roundingProfile;
	private String priceMarkingAgreed;
	private Boolean rackJobbingServiceAgreed;
	private Boolean orderEntryByVendor;
	private String servLevel;
	private String activityProfile;
	private String telephone;
	private String salesPerson;
	private Boolean subSeqSettIndex;
}
