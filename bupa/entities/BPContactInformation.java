package com.incture.bupa.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BP_CONTACT_INFORMATION")
public class BPContactInformation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_CONTACT_ID")
	private Integer contactId;
	
//	@Column(name = "BP_VENDOR_NO")
//	private String vendorNo;
	
	@Column(name="BP_BUPA_NO")
	private String bupaNo;
	
	@Column(name="BP_FORM_OF_ADDRESS")
	private String formOfAddress;
	
	@Column(name = "BP_FIRST_NAME")
	private String firstName;
	
	@Column(name = "BP_LAST_NAME")
	private String lastName;
	
	@Column(name = "BP_CONTACT_PERSON")
	private String contactPerson;
	
	@Column(name = "BP_TEL_NO")
	private String telephone;
	
	@Column(name = "BP_DEPARTMENT")
	private String department;
	
	@Column(name = "BP_FUNCTION")
	private String contactFunction;
	
	@Column(name="BP_MOBILE_NO")
	private String mobilePhone;
	
	@Column(name="BP_EMAIL")
	private String userEmail;
	//added
	@Column(name="BP_DESCRIPTION")
	private String description;
	
	@Column(name="BP_FUNCTION_DESCRIPTION")
	private String functionDescription;
	
	@Column(name="BP_TELEPHONE_CODE")
	private String telephoneCode;
	
	@Column(name="BP_IS_NEW")
	private Boolean isNew;
	
	
	@ManyToOne
    @JoinColumn(name = "BP_REQUEST_ID", referencedColumnName = "BP_REQUEST_ID")
//    @JsonBackReference
	private BPGeneralData bpGeneralData;
	
}
