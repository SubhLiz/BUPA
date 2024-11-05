package com.incture.bupa.entities;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_VENDOR_CLASSIFICATION_ENTITY")
public class BPVendorClassificationEntity {

	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "BP_VENDOR_CLASSIFICATION_ENTITY_ID")
	    private Integer vendorClassificationEntityId;

	    @Column(name="BP_CLASSNUM")
	    private String Classnum;//Classnum
	    
	    
	    @Column(name="BP_DESCRPTION")
		private String Description;
	    
	    @OneToMany(mappedBy="bpVendorClassificationEntity", cascade = CascadeType.ALL)
		private List<BPVendorClassificationAttribute> bpVendorClassificationAttribute=new ArrayList<>();
	    
	    @ManyToOne
	    @JoinColumn(name = "BP_REQUEST_ID", referencedColumnName = "BP_REQUEST_ID")
//	    @JsonBackReference
		private BPGeneralData bpGeneralData;
	}
