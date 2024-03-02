package com.enigma.wmb_api.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuRequest {
    private String name;
    private Long price;
    private Long minPrice;
    private Long maxPrice;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
