package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.dto.request.UpdateBillStatusRequest;
import com.enigma.wmb_api.dto.response.BillResponse;
import org.springframework.data.domain.Page;

public interface BillService {
    BillResponse create(BillRequest request);

    Page<BillResponse> findAll(BillRequest request);

    BillResponse findById(String id);

    void updateStatus(UpdateBillStatusRequest request);
}
