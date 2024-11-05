package com.incture.bupa.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIClassificationDto {
	@JsonProperty("ChangeIndObject")
    private String changeIndObject;

    @JsonProperty("Vendor")
    private String vendor;

    @JsonProperty("Classnum")
    private String classnum;

    @JsonProperty("Classtype")
    private String classtype;

    @JsonProperty("Object")
    private String object;

    @JsonProperty("Objecttable")
    private String objecttable;

    @JsonProperty("Keydate")
    private String keydate;
    
    @JsonProperty("Description")
    private String description;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Changenumber")
    private String changenumber;

    @JsonProperty("Stdclass")
    private String stdClass;

    @JsonProperty("Flag")
    private boolean flag;

    @JsonProperty("ObjectGuid")
    private String objectGuid;
    
    @JsonProperty("ToClassificationItem")
    public List<CPIClassificationItemDto> toClassificationItem;
}
