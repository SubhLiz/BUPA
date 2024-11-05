package com.incture.bupa.dto;

import lombok.Data;

@Data
public class FlexiWorkflowTaskDetailsDto {
	private String workflowTaskName;
	private String workflowApprovalLevel;
	private String userRecipient;
	private String flexibleTaskType;
	private String workflowApprover;
}
