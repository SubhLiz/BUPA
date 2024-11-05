package com.incture.bupa.dto;

import java.util.List;

import lombok.Data;
@Data
public class BPCompanyCodeInfoDto {
	private Integer companyCodeId;
//	private String vendorNo;
//	private String bupaNo;
//	private String correspondanceDetails;
	private String companyCode;
	private String whTaxCountry;
	private Boolean extend;
	private String companyCountry;
	private String branchCode;
	private String branchCodeDescription;
//	private String companyCodeInfo;
//	private String reconcilliationAccount;
//	private String interestInd;
//	private String wTaxCode;
//	private String whTaxCountry;
//	private String recipientType;
//	private String exemptionNo;
//	private Date validUntil;
//	private String exemptionAuthority;
//	private String paymentTerms;
//	private String toleranceGroup;
//	private String paymentMethods;
//	private String paymentBlock;
//	private String houseBank;
//	private String groupingKey;
//	private String accountingClerk;
//	private String accountingClerksFax;
//	private String actingClerksTelephone;
//	private String clerkAtSupplier;
//	private String planningGroup;
//	private String minorityIndicator;
//	private String clerkEmail;
//	private String reconcilliationAccountInGeneralLedger;
//	private String checkFlagForDoubleInvoiceOrCreditMemo;
//	private String companyCodeDeletionBlock;
//	private String sortKeyForPaymentAssignment;
//	private String accMemo;
//	private String accWithSupplier;
//	private String prevMasterRecNo;
//	private String termsPaymentKey;
//	private String delFlagMasterRecord;
//	private String companyCode;
	private List<BPWithholdingTaxDto> bpWithholdingTax;
	private BPAccountingInformationDto bpAccountingInformation;
	private BPPaymentTransactionsDto bpPaymentTransaction;
	private BPCorrespondanceDto bpCorrespondance;
}