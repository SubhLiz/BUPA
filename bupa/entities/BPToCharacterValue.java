//package com.incture.bupa.entities;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "BP_TO_CHARACTER_VALUE")
//public class BPToCharacterValue {
//	@Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "BP_TO_CHARACTER_VALUE_ID")
//    private Integer toCharacterValueId;
//	
//	@Column(name="BP_RESULTS")
//	private String results;
//	
//	@OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "BP_VENDOR_CLASSIFICATION_ATTRIBUTE_ID", referencedColumnName = "BP_VENDOR_CLASSIFICATION_ATTRIBUTE_ID")
//	private BPVendorClassificationAttribute bpVendorClassificationAttribute;
//}
