package com.incture.bupa.dto;

import lombok.Data;

@Data
public class ErrorDetailsDto {
	private Boolean success;
	private String errorMessage;
}
