package com.incture.bupa.utils;

import lombok.Data;

@Data
public class TaskOwnersDto {
	private String localApprover_RecipientUsers;
	private String gmdmApprover_RecipientUsers;
	private String financeApprover_RecipientUsers;
	private String gisApprover_RecipientUsers;
	private String sourcingApprover_RecipientUsers;
	private String categoryLeadApprover_RecipientUsers;
	private String regionalSourcingApprover_RecipientUsers;
	private String qualityApprover_RecipientUsers;
	private String requestor;

}
