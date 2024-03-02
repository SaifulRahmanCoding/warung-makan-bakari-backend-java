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
    // @NotBlank(message = "name is required")
    // private String name;
    //
    // @NotBlank(message = "mobile phone number is required")
    // private String mobilePhoneNo;
    //
    // @NotNull(message = "status is required")
    // private Boolean isMember;
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
