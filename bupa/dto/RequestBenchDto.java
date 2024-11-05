package com.incture.bupa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBenchDto {

    private Long totalCount;
    private Integer totalPages;
    private List<BPTaskBenchDataDto> bpTaskBenchData;

}
