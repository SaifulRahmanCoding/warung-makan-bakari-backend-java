package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.BILL_API)
public class BillController {
    private final BillService billService;

    @PostMapping
    public ResponseEntity<CommonResponse<BillResponse>> createBill(@RequestBody BillRequest request) {
        BillResponse newBill = billService.create(request);
        CommonResponse<BillResponse> response = CommonResponse.<BillResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("successfully create new bill")
                .data(newBill)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<BillResponse>> findAllBill() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<BillResponse>> findBillById(@PathVariable String id) {
        return null;
    }
}
