package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuRequest {
    @NotBlank
    private String name;
    @NotNull
    private Long price;
    private Long minPrice;
    private Long maxPrice;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
