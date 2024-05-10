package com.enigma.wmb_api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillResponse {
    private String id;
    private String transDate;
    private String customerId;
    private String customerName;
    private String tableId;
    private String tableName;
    private String transType;
    private List<BillDetailResponse> billDetails;
    private PaymentResponse paymentResponse;
}
