package com.incture.bupa.controller;

import com.incture.bupa.dto.BPAdvanceRequestSearchCriteriaDto;
import com.incture.bupa.dto.BPRequestSearchCriteriaDto;
import com.incture.bupa.dto.RequestBenchDto;
import com.incture.bupa.entities.BPGeneralData;
import com.incture.bupa.service.BPDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("*")
public class GetAllDetailsController {

    @Autowired
    private BPDetailService bpDetailsService;
    
    @PostMapping("/searchRequestData")
    public RequestBenchDto getAdvanceSearchRequestData(@RequestBody BPAdvanceRequestSearchCriteriaDto bpAdvanceRequestSearchCriteriaDto) {
        return bpDetailsService.getAdvanceSearchRequestData(bpAdvanceRequestSearchCriteriaDto);
    }

}
