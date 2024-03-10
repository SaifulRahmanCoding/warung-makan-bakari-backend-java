package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillRequest {
    @NotBlank
    private String customerId;

    private String tableId;

    @NotNull
    private List<BillDetailRequest> billDetails;

    private String transDate;
    private String startDate;
    private String endDate;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;

}
