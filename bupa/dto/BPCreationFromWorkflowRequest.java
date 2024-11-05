package com.incture.bupa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BPCreationFromWorkflowRequest {
	private String requestId;
	private String bpRequestType;
	private String subProcessType;
	private String vendorAccountGroup;
	private String businessPartnerNumber;
	private String businessPartnerName;
	private String requestorEmail;
	private String companyCode;
	private String systemId;
	private String vendorType;
	private String accountGrp;
	private String countryCode;
	// Changes Done : Author - Dheeraj Kumar ( Added Country Name )
	private String countryName;
	private String purchasingOrg;
	private String approvalDate;
	private boolean validate;
}
