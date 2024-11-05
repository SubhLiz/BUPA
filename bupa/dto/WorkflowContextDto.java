package com.incture.bupa.dto;

import lombok.Data;

@Data
public class WorkflowContextDto {
	private BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest;
	private WorkflowTaskDetailsDto workflowTaskDetails;
	private WorkflowConditionsDetailDto workflowConditionsDetail;
	private ErrorDetailsDto errorDetails;

}
