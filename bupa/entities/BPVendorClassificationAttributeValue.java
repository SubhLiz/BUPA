package com.incture.bupa.entities;

import java.util.List;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_VENDOR_CLASSIFICATION_ATTRIBUTE_VALUE")
public class BPVendorClassificationAttributeValue {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BP_VENDOR_CLASSIFICATION_ATTRIBUTE_VALUE_ID")
    private Integer vendorClassificationAttributeValueId;
	
	@Column(name="BP_ATTRIBUTE_VALUE")
	private String attributeValue;
	
//	@ManyToOne
//	@JoinColumn(name = "BP_VENDOR_CLASSIFICATION_ATTRIBUTE_ID", referencedColumnName = "BP_VENDOR_CLASSIFICATION_ATTRIBUTE_ID")
//	private BPVendorClassificationAttribute bpVendorClassificationAttribute;
}
