package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPCommentsDto {
	private Integer commentID;
	private String comment;
	private String commentedBy;
	private String commentedOn;
}
