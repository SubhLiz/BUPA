package com.incture.bupa.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_USER_ACTION")
public class BPUserAction {
	
	@Id    
    @Column(name = "BP_USER_ACTION_ID")
    private String actionID=UUID.randomUUID().toString();
	
	@Column(name="BP_REQUEST_ID")
	private String requestId;
	
	@Column(name = "BP_USER_EMAIL")
	private String userEmail;
	
	@Column(name="BP_USER_PERFORMED_STEPS") 
	@Lob 
	private String userPerformedSteps;
}