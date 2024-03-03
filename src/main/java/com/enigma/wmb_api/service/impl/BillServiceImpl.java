package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.EnumTransType;
import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.dto.response.BillDetailResponse;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.BillRepository;
import com.enigma.wmb_api.service.*;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final BillDetailService billDetailService;
    private final CustomerService customerService;
    private final MsTableService tableService;
    private final TransTypeService transTypeService;
    private final MenuService menuService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BillResponse create(BillRequest request) {
        validationUtil.validate(request);
        // butuh customer, table dan trans type
        Customer customer = customerService.findById(request.getCustomerId());
        TransType transType;

        if (request.getTableId() != null) transType = transTypeService.findOrSave(EnumTransType.DI);
        else transType = transTypeService.findOrSave(EnumTransType.TA);

        MsTable table = (request.getTableId() != null) ? tableService.findById(request.getTableId()) : null;

        Bill trx = Bill.builder()
                .transDate(new Date())
                .customer(customer)
                .table(table)
                .transType(transType)
                .build();
        billRepository.saveAndFlush(trx);

        List<BillDetail> trxDetails = request.getBillDetails().stream()
                .map(detailRequest -> {
                    Menu menu = menuService.findById(detailRequest.getMenuId());
                    return BillDetail.builder()
                            .bill(trx)
                            .menu(menu)
                            .qty(detailRequest.getQty())
                            .price(menu.getPrice())
                            .build();
                }).toList();
        trx.setBillDetails(trxDetails);
        billDetailService.createBulk(trxDetails);

        List<BillDetailResponse> trxDetailResponse = trxDetails.stream()
                .map(billDetail -> {
                    return BillDetailResponse.builder()
                            .id(billDetail.getId())
                            .menuId(billDetail.getMenu().getId())
                            .price(billDetail.getPrice())
                            .quantity(billDetail.getQty())
                            .build();
                }).toList();

        return BillResponse.builder()
                .id(trx.getId())
                .transDate(trx.getTransDate())
                .customerId(trx.getCustomer().getId())
                .tableId((request.getTableId() != null) ? trx.getTable().getId() : null)
                .transType(trx.getTransType().getId().name())
                .billDetails(trxDetailResponse)
                .build();
    }

    @Override
    public Page<BillResponse> findAll(BillRequest request) {
        return null;
    }

    @Override
    public BillResponse findById(BillRequest request) {
        return null;
    }
}
