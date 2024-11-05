package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "definitionId",
    "context"
})
public class LaunchWorkflow {

    @JsonProperty("definitionId")
    private String definitionId;
    @JsonProperty("context")
    private Context context;

    @JsonProperty("definitionId")
    public String getDefinitionId() {
        return definitionId;
    }

    @JsonProperty("definitionId")
    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    @JsonProperty("context")
    public Context getContext() {
        return context;
    }

    @JsonProperty("context")
    public void setContext(Context context) {
        this.context = context;
    }

}
