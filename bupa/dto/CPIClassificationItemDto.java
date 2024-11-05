package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIClassificationItemDto {
	@JsonProperty("ChangeIndObject")
    private String changeIndObject;

    @JsonProperty("Vendor")
    private String vendor;

    @JsonProperty("Charact")
    private String charact;

    @JsonProperty("ValuChar")
    private String valuChar;

    @JsonProperty("Inherited")
    private String inherited;

    @JsonProperty("Instance")
    private String instance;

    @JsonProperty("ValueNeutral")
    private String valueNeutral;

    @JsonProperty("CharactDescr")
    private String charactDescr;

    @JsonProperty("ValueCharLong")
    private String valueCharLong;

    @JsonProperty("ValueNeutralLong")
    private String valueNeutralLong;
}
