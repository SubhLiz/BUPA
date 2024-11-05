package com.incture.bupa.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class WorkflowResponseDto {
//	private int ac;
//	private ArrayList<String>al;
//	private ArrayList<String>atn;
	
	private List<Map<String,Object>> approvalFlexible;
}
