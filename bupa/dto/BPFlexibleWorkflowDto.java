package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPFlexibleWorkflowDto {
	private int flexibleWorkflowId;
	private String requestId;
	private String flexibleTaskType;
	private String workflowApprovalLevel;
	private String workflowTaskName;
	private String workflowApprover;
}
