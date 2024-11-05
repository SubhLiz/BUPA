package com.incture.bupa.dto;

import lombok.Data;
@Data
public class BPContactInformationDto {
	
	private Integer contactId;
//	private String vendorNo;
	private String bupaNo;
	private String formOfAddress;
	private String firstName;
	private String lastName;
	private String telephone;
	private String contactPerson;
	private String department;
	private String contactFunction;
	private String mobilePhone;
	private String userEmail;
	private String description;
	private String functionDescription;
	private String telephoneCode;
	private Boolean isNew;
}
