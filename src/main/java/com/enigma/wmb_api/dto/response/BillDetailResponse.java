package com.enigma.wmb_api.dto.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDetailResponse {
    private String id;
    private Long price;
    private String menuId;
    private Integer quantity;
}
