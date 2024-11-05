package com.incture.bupa.entities;


import javax.persistence.*;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_COMMENTS")
public class BPComments {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_COMMENT_ID")
	private Integer commentID;

	@Column(name = "BP_COMMENT_DESC",length = 500)
	private String comment;

	@Column(name = "BP_COMMENTED_BY",length = 100)
	private String commentedBy;
	
	
	@Column(name = "BP_COMMENTED_ON",length = 50)
	private String commentedOn;
	
	 @ManyToOne
	 @JoinColumn(name = "BP_REQUEST_ID", referencedColumnName = "BP_REQUEST_ID")
//	 @JsonBackReference
	 private BPGeneralData bpGeneralData;
}
