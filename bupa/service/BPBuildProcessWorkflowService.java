package com.incture.bupa.service;

import org.springframework.http.ResponseEntity;

import com.incture.bupa.dto.BPBuildProcessWorkflowContextDto;
import com.incture.bupa.dto.BPRequestGeneralDataDto;

public interface BPBuildProcessWorkflowService {
	void triggerOnboardingWorkflow(String requestId, BPRequestGeneralDataDto bpRequestGeneralDataDto);
	ResponseEntity<Object> triggerWorkflow(BPBuildProcessWorkflowContextDto bpBuildProcessWorkflowContextDto);
}
