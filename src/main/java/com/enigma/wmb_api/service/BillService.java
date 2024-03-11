package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.dto.request.UpdateBillStatusRequest;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.dto.response.CSVBillResponse;
import com.enigma.wmb_api.entity.Bill;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface BillService {
    BillResponse create(BillRequest request);

    Page<BillResponse> findAll(BillRequest request);

    BillResponse findById(String id);

    void updateStatus(UpdateBillStatusRequest request);

    List<CSVBillResponse> findAllBillToCsv(BillRequest request);
}
