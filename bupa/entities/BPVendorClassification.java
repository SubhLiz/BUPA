package com.incture.bupa.entities;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_VENDOR_CLASSIFICATION")
public class BPVendorClassification {

	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "BP_VENDOR_CLASSIFICATION_ID")
	    private Integer vendorClassificationId;

	    @Column(name="BP_ENTITY_NAME")
	    private String entityName;
	    @Column(name="BP_ATTRIBUTE_NAME")
	    private String attributeName;
	    @Column(name="BP_ATTRIBUTE_VALUE")
	    private String attributeValue;
	    @Column(name="BP_ENTITY_NAME_LIST")
	    private String entityNameList;
	    
	    @ManyToOne
	    @JoinColumn(name = "BP_REQUEST_ID", referencedColumnName = "BP_REQUEST_ID")
//	    @JsonBackReference
		private BPGeneralData bpGeneralData;
	}
