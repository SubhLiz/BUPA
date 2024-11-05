package com.incture.bupa.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
@Data
public class BPPurchasingOrgDetailDto {
	private Integer purchaseId;
//	private String vendorNo;
//	private String bupaNo;
//	private String purchaseDetails;
	private String purchasingOrg;
	private Boolean extend;
	private Boolean extendAdditionalData;
	private Boolean orderingAddressCheck;
	private Boolean remittanceAddressCheck;
	private String estimatedAnnualSpend;
	private String sourcingCategory;
//	private String purchaseOrg;
//	private String poCurrency;
//	private String termsPayment;
//	private String incoTerms;
//	private String incoTerms2;
//	private String priceDataControl;
//	private String accountWithVendor;
//	private String shippingCondition;
//	private String purchaseGrp;
//	private Date plannedDeliveryDate;
//	private String confirmationControl;
//	private String unitOfMeasureGroup;
//	private String roundProfile;
//	private String podf;
//	private String igrbiv;
//	private String abcIndicator;
//	private String responsibleSalesPerson;
//	private String salesPersonTelephoneNumber;
//	private String oar;
//	private String automaticPurchaseOrderGeneration;
//	private String plant;
//	private String supplierSubRange;
//	private String supplierCurrency;
//	private String acknowledgementRequired;
//	private String grBasedIv;
//	private String motBorder;
	private BPPurchaseOrgDto bpPurchaseOrg;
	private ArrayList<BPPurchaseOrgAdditionalDataDto> bpPurchaseOrgAdditionalData;
	private BPBusinessPartnerOrderingAddressDto bpBusinessPartnerOrderingAddress;
	private BPBusinessPartnerRemittanceAddressDto bpBusinessPartnerRemittanceAddress;
}