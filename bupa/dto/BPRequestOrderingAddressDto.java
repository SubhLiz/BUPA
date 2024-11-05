package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPRequestOrderingAddressDto {
	private Integer addressId;
	private String street;
	private String houseNo;
	private String district;
	private int postalCode;
	private String city;
	private String region;
	private String country;
	private String timeZone;
	private String language;
	private String telephoneNumber;
	private String mobileNumber;
	private String telephoneCode;
	private String mobileCode;
	private String faxNumber;
	private String email;
	private String addressType;
	private String standardCommMethod;
	private String paymentTerms;
	private String addressName;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String addressLine4;
	private String addressLine5;
//	private String vendorNo;
	private String bupaNo;
	private String saveAsDraft;
	private String phoneExt;
	private String commMethod;
}
