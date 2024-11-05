package com.incture.bupa.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_PAYMENT_TRANSACTION")
public class BPPaymentTransactions {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_PAYMENT_TRANSACTION_ID")
	private Integer paymentTransactionId;
	
	@Column(name = "BP_PAYMENT_TERM")
	private String paymentTerms;
	
	@Column(name = "BP_PAY_DATA_TOLERANCE_GRP")
	private String payDataToleranceGroup;
	
	@Column(name="BP_CHECK_DOUBLE_INVOICE")
	private String chkDoubleInv;
	
	@Column(name = "BP_PAYMENT_METHODS")
	private String paymentMethods;
	
	@Column(name = "BP_PAYMENT_BLOCK")
	private String paymentBlock;
	
	@Column(name="BP_ALTERNATE_PAYEE")
	private String alternatePayee;
	
	@Column(name = "BP_HOUSE_BANK")
	private String houseBank;
	
	@Column(name = "BP_GROUPING_KEY")
	private String groupingKey;
	
	@Column(name="BP_BEXCH_LIMIT")
	private String bExchLimit;
	
	@Column(name="BP_INDIVIDUAL_PERMIT")
	private Boolean individualPermit;
	
	@Column(name="BP_PMT_ADV_BYEDI")
	private Boolean pmtAdvByEDI;
	
	@Column(name="BP_PMTMETHSUPL")
	private String pmtmethsupl;
	
	@Column(name = "BP_INV_VER_TOLERANCE_GRP")
	private String invoiceVerificationToleranceGroup;
	
	@Column(name="BP_ASSIGN_GRP")
	private String assignGrp;
	
	@Column(name="BP_PRE_PAYMENT")
	private String prePayment;
	
	@Column(name="BP_CHK_CASHING_TIME")
	private String chkCashingTime;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "BP_COMP_CODE_INFO_ID", referencedColumnName = "BP_COMP_CODE_INFO_ID")
	private BPCompanyCodeInfo bpCompanyCodeInfo;
}
