package com.incture.bupa.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_COMMUNICATION")
public class BPCommunication {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_COMMUNICATION_ID")
	private Integer communicationId;
	
	@OneToMany(mappedBy="bpCommunication", cascade = CascadeType.ALL)
	private List<BPTelephone> bpTelephone=new ArrayList<>();
	

	@OneToMany(mappedBy="bpCommunication", cascade = CascadeType.ALL)
	private List<BPMobilePhone> bpMobilePhone=new ArrayList<>();
	

	@OneToMany(mappedBy="bpCommunication", cascade = CascadeType.ALL)
	private List<BPEmail> bpEmail=new ArrayList<>();

	@OneToMany(mappedBy="bpCommunication", cascade = CascadeType.ALL)
	private List<BPFaxInfo> bpFaxInfo=new ArrayList<>();
	
//	@Column(name = "BP_COMMENTS")
//	private String comments;
	

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bp_request_id", referencedColumnName = "BP_REQUEST_ID")
	private BPGeneralData bpGeneralData;
}
