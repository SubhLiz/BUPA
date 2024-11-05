package com.incture.bupa.dto;

import java.util.List;

import lombok.Data;
@Data
public class BPBuildProcessWorkflowContextDto {
	private String requestId;

	private String requestorName;

	private String email;

	private String createdDate;

	private List<String> localTeamEmailGroup;

	private List<String> gmdmTeamEmailGroup;

}
