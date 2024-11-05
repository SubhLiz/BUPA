package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPCorrespondanceDto {
	private Integer correspondanceId;
	private String dunnProcedure;
	private String dunningBlock;
	private String dunnRecepient;
	private String legalDunnProc;
	private String lastDunned;
	private Integer dunningLevel;
	private String dunningClerk;
	private Boolean localProcess;
	private String groupingKeys;
	private String accountingClerk;
	private String acctwVendor;
	private String clerkAtVendor;
	private String actingClerksTelephone;
	private String clerkFax;
	private String clerkInternet;
	private String accountMemo;
}
