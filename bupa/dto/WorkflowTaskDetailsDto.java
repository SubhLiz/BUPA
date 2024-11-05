package com.incture.bupa.dto;

import java.util.List;

import lombok.Data;

@Data
public class WorkflowTaskDetailsDto {
	private String localApprover_RecipientUsers;
	private String gmdmApprover_RecipientUsers;
	private String financeApprover_RecipientUsers;
	private String gisApprover_RecipientUsers;
	private String sourcingApprover_RecipientUsers;
	private String categoryLeadApprover_RecipientUsers;
	private String regionalSourcingApprover_RecipientUsers;
	private String qualityApprover_RecipientUsers;
	private String requestor;
	private String qualityWorkflowApproverGroup;
	private String sourcingWorkflowApproverGroup;
	private String gmdmApproverGroup;
	private String financeApproverGroup;
	private String categoryLeadApproverGroup;
	private List<FlexiWorkflowTaskDetailsDto> flexiWorkflowTaskDetails;
}
