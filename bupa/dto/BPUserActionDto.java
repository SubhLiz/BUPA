package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPUserActionDto {
	private String requestId;
	private String userEmail;
	private String userPerformedSteps;
}
