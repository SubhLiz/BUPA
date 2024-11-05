package com.incture.bupa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_FLEXIBLE_WORKFLOW")
public class BPFlexibleWorkflow {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_FLEXIBLE_WORKFLOW_ID")
	private Integer flexibleWorkflowId;
	
	@Column(name="BP_REQUEST_ID")
	private String requestId;
	
	@Column(name="BP_FLEXIBLE_TASK_TYPE")
	private String flexibleTaskType;
	
	@Column(name = "BP_WORKFLOW_APPROVAL_LEVEL")
	private String workflowApprovalLevel;
	
	@Column(name = "BP_WORKFLOW_TASK_NAME")
	private String workflowTaskName;
	
	@Column(name = "BP_WORKFLOW_APPROVER")
	private String workflowApprover;
}
