package com.incture.bupa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BPUserDetailsDto {

    private String userEmail;
    private String userName;

}
