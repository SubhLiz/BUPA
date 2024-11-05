package com.incture.bupa.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_ADDRESS_INFO")
public class BPAddressInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_ADDRESS_INFO_ID")
	private Integer addressId;
	
//	@Column(name = "BP_VENDOR_NO")
//	private String vendorNo;
	
	@Column(name="BP_BUPA_NO")
	private String bupaNo;
	
	@Column(name = "BP_STREET")
	private String street;
	
	@Column(name = "BP_HOUSE_NO")
	private String houseNo;
	
	@Column(name = "BP_DISTRICT")
	private String district;
	
	@Column(name = "BP_POSTAL_CODE")
	private String postalCode;
	
	@Column(name = "BP_CITY")
	private String city;
	
	@Column(name = "BP_REGION")
	private String region;
	
	@Column(name = "BP_COUNTRY")
	private String country;
	
	@Column(name = "BP_TIME_ZONE")
	private String timeZone;
	
	@Column(name = "BP_LANGUAGE")
	private String language;
	
	@Column(name = "BP_FIRST_TEL_NO")
	private String telephoneNumber;
	
	@Column(name="BP_FIRST_TEL_NO_CODE")
	private String telephoneCode;
	
	@Column(name="BP_MOBILE_PHONE")
	private String mobileNumber;
	
	@Column(name="BP_MOBILE_PHONE_CODE")
	private String mobileCode;
	
	@Column(name = "BP_FIRST_FAX_NO")
	private String faxNumber;
	
	@Column(name = "BP_STD_COMM_METHOD")
	private String standardCommMethod;
	
	@Column(name="BP_EMAIL")
	private String email;
	
	@Column(name="BP_ADDRESS_TYPE")
	private String addressType;
	
	@Column(name="BP_PREFERRED_PAYMENT_TERMS")
	private String paymentTerms;
	
	@Column(name="BP_ADDRESS_NAME")
	private String addressName;
	
	@Column(name="BP_ADDRESS_LINE_1")
	private String addressLine1;
	
	@Column(name="BP_ADDRESS_LINE_2")
	private String addressLine2;
	
	@Column(name="BP_ADDRESS_LINE_3")
	private String addressLine3;
	
	@Column(name="BP_ADDRESS_LINE_4")
	private String addressLine4;
	
	@Column(name="BP_ADDRESS_LINE_5")
	private String addressLine5;
	
	@Column(name="BP_COMMUNICATION_METHOD")
	private String commMethod;
	
	@Column(name="BP_PHONE_EXTENSION")
	private String phoneExt;

	@Column(name = "BP_SEQUENCE_NUMBER")
	private String sequenceNumber;

	@Column(name = "BP_STANDARD_ADDRESS_INDICATOR")
	private String standardAddressIndicator;

	@Column(name = "BP_DO_NOT_USE")
	private String doNotUse;

	@Column(name = "BP_NOTES")
	private String notes;
	
	//added
	@Column(name="BP_BUILDING_CODE")
	private String buildingCode;
	
	@Column(name="BP_FLOOR")
	private String floor;
	
	@Column(name="BP_CO")
	private String co;
	
	@Column(name="BP_SUPPL")
	private String suppl;
	
	@Column(name="BP_ROOM")
	private String room;
	
	@Column(name="BP_STREET2")
	private String street2;
	
	@Column(name="BP_STREET3")
	private String street3;
	
	@Column(name="BP_STREET4")
	private String street4;
	
	@Column(name="BP_STREET5")
	private String street5;
	
	@Column(name="BP_DIFFERENT_CITY")
	private String differentCity;
	
	@Column(name="BP_TAX_JURISDICTION")
	private String taxJurisdiction;
	
	@Column(name="BP_TRNSPORT_ZONE")
	private String transportZone;
	
	@Column(name="BP_REG_STRUCT_GRP")
	private String regStructGrp;
	
	@Column(name="BP_UNDELIVERABLE")
	private String undeliverable;
	
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BP_REQUEST_ID", referencedColumnName = "BP_REQUEST_ID")
//    @JsonBackReference
	private BPGeneralData bpGeneralData;
	
}
