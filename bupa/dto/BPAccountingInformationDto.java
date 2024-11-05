package com.incture.bupa.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BPAccountingInformationDto {
	private Integer accountingInformationId;
	private String reconcilliationAccountInGeneralLedger;
	private String sortKey;
	private String headOffice;
	private String authorization;
	private String cashManagementGroup;
	private String releaseGroup;
	private String minorityIndicator;
	private String certificationDate;
	private String interestInd;
	private String lastKeyDate;
	private String interestCycle;
	private String lastInterestRun;
	private String wtaxCode;
	private String exemptionNumber;
	private String whTaxCountry;
	private String validUntil; 
	private String recipientType;
	private String exemptionAuthority;
	private String prevAcctNo;
	private String personnelNumber;
}
