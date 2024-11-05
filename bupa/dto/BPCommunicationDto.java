package com.incture.bupa.dto;

import java.util.ArrayList;

import lombok.Data;

@Data
public class BPCommunicationDto {
	private Integer communicationId;
	private ArrayList<BPTelephoneDto> bpTelephone;
	private ArrayList<BPMobilePhoneDto> bpMobilePhone;
	private ArrayList<BPEmailDto> bpEmail;
	private ArrayList<BPFaxInfoDto> bpFaxInfo;
	//private String comments;
}
