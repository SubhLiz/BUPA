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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_EMAIL")
public class BPEmail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BP_EMAIL_ID")
    private Integer emailId;

    @Column(name = "BP_EMAIL_ADDRESS")
    private String emailAddress;

    @Column(name = "BP_STANDARD_NUMBER")
    private Boolean standardNumber;

    @Column(name = "BP_DO_NOT_USE")
    private boolean doNotUse;

    @Column(name = "BP_NOTES")
    private String notes;

    @Column(name = "BP_ID")
    private String id;
    
    @ManyToOne
   	@JoinColumn(name = "BP_COMMUNICATION_ID", referencedColumnName = "BP_COMMUNICATION_ID")
   	private BPCommunication bpCommunication;
}
