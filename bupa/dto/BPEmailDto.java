package com.incture.bupa.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Data
@Getter
@Setter
public class BPEmailDto {

    private Integer emailId;
    private String emailAddress;
    private Boolean standardNumber;
    private boolean doNotUse;
    private String notes;
    private String id;

}
