package com.incture.bupa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class WorkflowConditionsDetailDto {
	private Boolean changePaymentTerms;
	private Boolean extendPurchaseOrg;
	private Boolean checkSourcing;
	private Boolean checkBonafide;
	private Boolean isGeneric;
	private Boolean changeBankDetails;
	private Boolean isAddnDataPresent;
	private Boolean isBankDetailsPresent;
	private Boolean isExtend;
	private Boolean isChange;
}
