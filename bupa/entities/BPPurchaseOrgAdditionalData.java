package com.incture.bupa.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_PURCHASE_ORG_ADDITIONAL_DATA")
public class BPPurchaseOrgAdditionalData {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_PURCHASE_ORG_ADDITIONAL_DATA_ID")
	private Integer purchaseOrgAddDataId;
	
	
	@Column(name="BP_PLANT")
	private String plant;
	
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
	
	@Column(name="BP_PRICING_DATE_CONTROL")
	private String pricingDateControl;
	
	@Column(name="BP_ORDER_OPTIM_REST")
	private String orderOptimRest;
	
	@Column(name="BP_ABC_INDICATOR")
	private String abcIndicator;
	
	@Column(name="BP_MODE_OF_TRANSPORT_BORDER")
	private String modeofTransportBorder;
	
	@Column(name="BP_OFFICE_OF_ENTRY")
	private String officeOfEntry;
	
	@Column(name="BP_PROACT_CONTROL_PROF")
	private String proActControlProf;
	
	@Column(name="BP_EXTEND")
	private Boolean extend;
	
	@Column(name="BP_GR_BASED_INV_VERIF")
	private Boolean grBasedInvVerify;
	
	@Column(name="BP_AUTOEVALGRSETMT_DEL")
	private Boolean autoEvalGRSetMtDel;
	
	@Column(name="BP_AUTOEVALGRSETMT_RET")
	private Boolean autoEvalGRSetMtRet;
	
	@Column(name="BP_ACKNOWLEDGEMENT_REQD")
	private Boolean acknowledgementReqd;
	
	@Column(name="BP_AUTOMATIC_PURCHASE_ORDER")
	private Boolean automaticPurchaseOrder;
	
	@Column(name="BP_SRV_BASED_INV_VAR")
	private Boolean srvBasedInvVar;
	
	@Column(name="BP_REVALUATION")
	private Boolean revaluation;
	
	@Column(name="BP_PURCHASING_GROUP")
	private String purchasingGroup;
	
	@Column(name="BP_MRP_CONTROLLER")
	private String mrpController;
	
	@Column(name="BP_PLANNED_DELIV_TIME")
	private String planneddelivtime;
	
	@Column(name="BP_DELIVERY_CYCLE")
	private String deliveryCycle;
	
	@Column(name="BP_PLANNING_CYCLE")
	private String planningCycle;
	
	@Column(name="BP_CONFIRMATION_CONTROL")
	private String confirmationControl;
	
	@Column(name="BP_UNIT_OF_MEASURE_GROUP")
	private String unitofMeasureGroup;
	
	@Column(name="BP_ROUNDING_PROFILE")
	private String roundingProfile;
	
	@Column(name="BP_CREATION_PROFILE")
	private String creationProfile;
	
	@Column(name="BP_VENDOR_PRICE_MARKING")
	private String vendorPriceMarking;
	
	@Column(name="BP_RACK_JOBBING_SERVICE_AGREED")
	private Boolean rackJobbingServiceAgreed;
	
	@Column(name="BP_ORDER_ENTRY_BY_VENDOR")
	private Boolean orderEntryByVendor;

//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "BP_PURCHASING_ORG_DETAIL_ID", referencedColumnName = "BP_PURCHASING_ORG_DETAIL_ID")
//	private BPPurchasingOrgDetail bpPurchasingOrgDetail;
	@ManyToOne
	@JoinColumn(name = "BP_PURCHASING_ORG_DETAIL_ID", referencedColumnName = "BP_PURCHASING_ORG_DETAIL_ID")
	private BPPurchasingOrgDetail bpPurchasingOrgDetail;
}