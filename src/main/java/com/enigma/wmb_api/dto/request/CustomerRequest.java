package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String mobilePhoneNo;
    @NotNull
    private Boolean isMember;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
