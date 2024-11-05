package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPPaymentTransactionsDto {
	private Integer paymentTransactionId;
	private String paymentTerms;
	private String payDataToleranceGroup;
	private Boolean chkDoubleInv;
	private String chkCashingTime;
	private String paymentMethods;
	private String paymentBlock;
	private String alternatePayee;
	private String houseBank;
	private String groupingKey;
	private String bExchLimit;
	private Boolean individualPermit;
	private Boolean pmtAdvByEDI;
	private String pmtmethsupl;
	private String invoiceVerificationToleranceGroup;
	private String assignGrp;
	private String prePayment;
}
