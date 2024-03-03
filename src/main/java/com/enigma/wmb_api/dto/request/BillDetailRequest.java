package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDetailRequest {
    @NotBlank
    private String menuId;
    @NotNull
    private Integer qty;
}
