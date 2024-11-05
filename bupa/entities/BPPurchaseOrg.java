package com.incture.bupa.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_PURCHASE_ORG")
public class BPPurchaseOrg {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_PURCHASE_ORG_ID")
	private Integer purchaseOrgId;
	
	@Column(name="BP_PURCHASING_ORG")
	private String purchasingOrg;
	
	@Column(name="BP_ORDER_CURRENCY")
	private String orderCurrency;
	
	@Column(name="BP_TERMS_OF_PAYMENT")
	private String termsOfPayment;
	
	@Column(name="BP_INCO_TERMS")
	private String incoTerms;
	
	@Column(name="BP_INCO_TERMS2")
	private String incoTerms2;
	
	@Column(name="BP_MIN_ORDER_VALUE")
	private String minOrderValue;
	
	@Column(name="BP_SCHEMA_GROUP_VENDOR")
	private String schemaGroupVendor;
	
	@Column(name="BP_PRICING_DATE_CONTROL")
	private String pricingDateControl;
	
	@Column(name="BP_ORDEROPTIM_REST")
	private String orderOptimRest;
	
	@Column(name="BP_ACC_WITH_VENDOR")
	private String accWithVendor;
	
	@Column(name="BP_ABC_INDICATOR")
	private String abcIndicator;
	
	@Column(name="BP_MODE_OF_TRANSPORT_BORDER")
	private String modeOfTransportBorder;
	
	@Column(name="BP_OFFICE_OF_ENTRY")
	private String officeOfEntry;
	
	@Column(name="BP_SORT_CRITERION")
	private String sortCriterion;
	
	@Column(name="BP_PROACT_CONTROL_PROF")
	private String proActControlProf;
	
	@Column(name="BP_SHIPPING_CONDITIONS")
	private String shippingConditions;
	
	@Column(name="BP_GR_BASED_INV_VERIFY")
	private Boolean grBasedInvVerify;
	
	@Column(name="BP_AUTO_EVAL_GR_SET_MT_DEL")
	private Boolean autoEvalGRSetMtDel;
	
	@Column(name="BP_AUTO_EVAL_GRSETMT_RET")
	private Boolean autoEvalGRSetMtRet;
	
	@Column(name="BP_ACKNOWLEDGEMENT_REQD")
	private Boolean acknowledgementReqd;
	
	@Column(name="BP_AUTOMATIC_PURCHASE_ORDER")
	private Boolean automaticPurchaseOrder;
	
	@Column(name="BP_SUBSEQUENT_SETTLEMENT")
	private Boolean subsequentSettlement;
	
	@Column(name="BP_BVOL_COMP")
	private Boolean bVolComp;
	
	@Column(name="BP_DOC_INDEX_ACTIVE")
	private Boolean docIndexActive;
	
	@Column(name="BP_RETURNS_VENDOR")
	private Boolean returnsVendor;
	
	@Column(name="BP_SRV_BASED_INV_VAR")
	private Boolean srvBasedInvVar;
	
	@Column(name="BP_REVALUATION_ALLOWED")
	private Boolean revaluationAllowed;
	
	
	@Column(name="BP_GRANT_DISCOUNT_IN_KIND")
	private Boolean grantDiscountInKind;
	
	@Column(name="BP_RELEVANT_FOR_PRICE_DET")
	private Boolean relevantForPriceDet;
	
	@Column(name="BP_RELEVANT_FOR_AGENCY_BUSINESS")
	private Boolean relevantForAgencyBusiness;
	
	@Column(name="BP_PURCHASING_GROUP")
	private String purchasingGroup;
	
	@Column(name="BP_PLANNED_DELIV_TIME")
	private String plannedDelivTime;
	
	@Column(name="BP_CONFIRMATION_CONTROL")
	private String confirmationControl;
	
	@Column(name="BP_UNIT_OF_MEASURE_GROUP")
	private String unitOfMeasureGroup;
	
	@Column(name="BP_ROUNDING_PROFILE")
	private String roundingProfile;
	
	@Column(name="BP_PRICE_MARKING_AGREED")
	private String priceMarkingAgreed;
	
	@Column(name="BP_RACK_JOBBING_SERVICE_AGREED")
	private Boolean rackJobbingServiceAgreed;
	
	@Column(name="BP_ORDER_ENTRY_BY_VENDOR")
	private Boolean orderEntryByVendor;
	
	@Column(name="BP_SERV_LEVEL")
	private String servLevel;
	
	@Column(name="BP_ACTIVITY_PROFILE")
	private String activityProfile;
	
	@Column(name="BP_TELEPHONE")
	private String telephone;
	
	@Column(name="BP_SALES_PERSON")
	private String salesPerson;
	
	@Column(name="BP_SUB_SEQ_SETT_INDEX")
	private Boolean subSeqSettIndex;
	
	

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "BP_PURCHASING_ORG_DETAIL_ID", referencedColumnName = "BP_PURCHASING_ORG_DETAIL_ID")
	private BPPurchasingOrgDetail bpPurchasingOrgDetail;
}