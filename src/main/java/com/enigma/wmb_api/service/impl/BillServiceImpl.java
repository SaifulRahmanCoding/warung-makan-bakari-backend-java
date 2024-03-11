package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.EnumTransType;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.dto.request.UpdateBillStatusRequest;
import com.enigma.wmb_api.dto.response.*;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.BillRepository;
import com.enigma.wmb_api.service.*;
import com.enigma.wmb_api.specification.BillSpecification;
import com.enigma.wmb_api.util.DateUtil;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final BillDetailService billDetailService;
    private final CustomerService customerService;
    private final MsTableService tableService;
    private final TransTypeService transTypeService;
    private final MenuService menuService;
    private final ValidationUtil validationUtil;
    private final PaymentService paymentService;

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

        Payment payment = paymentService.createPayment(trx);
        trx.setPayment(payment);
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(payment.getId())
                .token(payment.getToken())
                .redirectUrl(payment.getRedirectUrl())
                .transactionStatus(payment.getBillStatus())
                .build();

        return BillResponse.builder()
                .id(trx.getId())
                .transDate(String.valueOf(trx.getTransDate()))
                .customerId(trx.getCustomer().getId())
                .tableId((request.getTableId() != null) ? trx.getTable().getId() : null)
                .transType(trx.getTransType().getId().name())
                .billDetails(trxDetailResponse)
                .paymentResponse(paymentResponse)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BillResponse> findAll(BillRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (DateUtil.parseDate(request.getStartDate(), "yyyy-MM-dd").getTime() > DateUtil.parseDate(request.getEndDate(), "yyyy-MM-dd").getTime())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date must be greater than start date");
        }

        if (request.getPage() <= 0) request.setPage(1);

        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());

        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);

        Specification<Bill> specification = BillSpecification.getSpecification(request);

        Page<Bill> bills = billRepository.findAll(specification, pageable);

        // buat bill detail response
        List<BillResponse> billResponses = bills.getContent().stream().map(this::getBillResponse).toList();
        return new PageImpl<>(billResponses, pageable, bills.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public BillResponse findById(String id) {
        Optional<Bill> optionalBill = billRepository.findById(id);
        if (optionalBill.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND);
        Bill bill = optionalBill.get();
        return getBillResponse(bill);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(UpdateBillStatusRequest request) {
        Bill bill = billRepository.findById(request.getOrderId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND));

        Payment payment = bill.getPayment();
        payment.setBillStatus(request.getBillStatus());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CSVBillResponse> findAllBillToCsv(BillRequest request) {
        Specification<Bill> specification = BillSpecification.getSpecification(request);
        // bingung cara join table dengan specification untuk dapat kolom billstatus dari payment, jadi pakai cara ini, walau cara ini tidak dianjurkan
        List<Bill> bills = billRepository.findAll(specification).stream().filter(bill -> bill.getPayment().getBillStatus().equals("settlement")).toList();

        List<CSVBillResponse> responses = new ArrayList<>();
        responses.add(CSVBillResponse.builder()
                .billId("bill_id")
                .transDate("transaction_date")
                .customerName("customer_name")
                .tableName("table")
                .transType("transaction_type")
                .menuName("menu_name")
                .quantity("quantity")
                .price("price")
                .build());

        bills.forEach(
                bill -> bill.getBillDetails().forEach(
                        billDetail -> {
                            CSVBillResponse buildResponse = CSVBillResponse.builder()
                                    .billId(bill.getId())
                                    .transDate(bill.getTransDate().toString())
                                    .customerName(bill.getCustomer().getName())
                                    .tableName((bill.getTable() != null) ? bill.getTable().getName() : "N/A")
                                    .transType(bill.getTransType().getDescription())
                                    .menuName(billDetail.getMenu().getName())
                                    .quantity(billDetail.getQty().toString())
                                    .price(billDetail.getPrice().toString())
                                    .build();
                            responses.add(buildResponse);
                        }
                )
        );
        return responses;
    }

    private BillResponse getBillResponse(Bill bill) {
        List<BillDetailResponse> billDetailResponses = bill.getBillDetails().stream()
                .map(detail -> BillDetailResponse.builder()
                        .id(detail.getId())
                        .menuId(detail.getMenu().getId())
                        .quantity(detail.getQty())
                        .price(detail.getPrice())
                        .build())
                .toList();

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(bill.getPayment().getId())
                .token(bill.getPayment().getToken())
                .redirectUrl(bill.getPayment().getRedirectUrl())
                .transactionStatus(bill.getPayment().getBillStatus())
                .build();

        return BillResponse.builder()
                .id(bill.getId())
                .customerId(bill.getCustomer().getId())
                .tableId((bill.getTable() != null) ? bill.getTable().getId() : null)
                .transDate(String.valueOf(bill.getTransDate()))
                .transType(bill.getTransType().getId().name())
                .billDetails(billDetailResponses)
                .paymentResponse(paymentResponse)
                .build();
    }
}
