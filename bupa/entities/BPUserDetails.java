package com.incture.bupa.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "BP_USER_DETAILS")
public class BPUserDetails {

    @Id
    @Column(name = "USER_EMAILID")
    private String userEmail;

    @Column(name = "USERNAME")
    private String userName;

}
