package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.EnumTransType;
import com.enigma.wmb_api.dto.request.BillDetailRequest;
import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.dto.request.UpdateBillStatusRequest;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.dto.response.CSVBillResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repository.BillRepository;
import com.enigma.wmb_api.service.*;
import com.enigma.wmb_api.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {
    @Mock
    private BillRepository billRepository;
    @Mock
    private BillDetailService billDetailService;
    @Mock
    private CustomerService customerService;
    @Mock
    private MsTableService tableService;
    @Mock
    private TransTypeService transTypeService;
    @Mock
    private MenuService menuService;
    @Mock
    private ValidationUtil validationUtil;
    @Mock
    private PaymentService paymentService;

    private BillService billService;

    @BeforeEach
    void setUp() {
        billService = new BillServiceImpl(billRepository, billDetailService, customerService, tableService, transTypeService, menuService, validationUtil, paymentService);
    }

    @Test
    void shouldReturnBillWhenCreate() {
        BillRequest request = BillRequest.builder()
                .customerId("cs-1")
                .tableId("tb-1")
                .billDetails(List.of(
                        BillDetailRequest.builder()
                                .menuId("menu-1")
                                .qty(3)
                                .build(),
                        BillDetailRequest.builder()
                                .menuId("menu-2")
                                .qty(3)
                                .build()
                ))
                .build();
        Mockito.doNothing().when(validationUtil).validate(request);

        Customer customer = Customer.builder()
                .userAccount(UserAccount.builder().build())
                .build();
        EnumTransType enumTransType = EnumTransType.DI;
        TransType transType = TransType.builder()
                .id(enumTransType)
                .build();

        Mockito.when(customerService.findById(request.getCustomerId())).thenReturn(customer);
        Mockito.when(transTypeService.findOrSave(enumTransType)).thenReturn(transType);

        MsTable table = MsTable.builder().build();
        Mockito.when(tableService.findById(request.getTableId())).thenReturn(table);

        Bill bill = Bill.builder()
                .transDate(new Date())
                .customer(customer)
                .table(table)
                .transType(transType)
                .build();
        Mockito.when(billRepository.saveAndFlush(Mockito.any())).thenReturn(bill);

        List<BillDetail> billDetails = new ArrayList<>();
        int increment = 0;
        int incMenu = 0;

        for (BillDetailRequest detail : request.getBillDetails()) {
            Menu menu = Menu.builder()
                    .id("menu-" + ++incMenu)
                    .build();
            Mockito.when(menuService.findById(detail.getMenuId())).thenReturn(menu);

            billDetails.add(BillDetail.builder()
                    .id("bill-td-" + ++increment)
                    .bill(bill)
                    .menu(menu)
                    .qty(detail.getQty())
                    .price(menu.getPrice())
                    .build());
        }
        Mockito.when(billDetailService.createBulk(Mockito.any())).thenReturn(billDetails);

        bill.setBillDetails(billDetails);

        Payment payment = Payment.builder().build();
        Mockito.when(paymentService.createPayment(Mockito.any())).thenReturn(payment);

        bill.setPayment(payment);
        // when
        BillResponse response = billService.create(request);

        // then
        assertNotNull(response);
        assertEquals("menu-2", response.getBillDetails().get(1).getMenuId());
    }

    @Test
    void shouldReturnBillWhenFindAll() {
        BillRequest request = BillRequest.builder()
                .startDate("2024-01-01")
                .endDate("2024-01-29")
                .page(2)
                .size(3)
                .sortBy("name")
                .direction("asc")
                .build();
        List<Bill> bills = List.of(
                Bill.builder()
                        .customer(Customer.builder().build())
                        .table(MsTable.builder().build())
                        .transType(TransType.builder().id(EnumTransType.DI).build())
                        .transDate(new Date())
                        .payment(Payment.builder().build())
                        .billDetails(List.of(
                                BillDetail.builder().menu(Menu.builder().build()).build(),
                                BillDetail.builder().menu(Menu.builder().build()).build()
                        ))
                        .build(),
                Bill.builder()
                        .customer(Customer.builder().build())
                        .table(MsTable.builder().build())
                        .transType(TransType.builder().id(EnumTransType.DI).build())
                        .transDate(new Date())
                        .payment(Payment.builder().build())
                        .billDetails(List.of(
                                BillDetail.builder().menu(Menu.builder().build()).build(),
                                BillDetail.builder().menu(Menu.builder().build()).build()
                        ))
                        .build()
        );

        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Page<Bill> page = new PageImpl<>(bills);

        Mockito.when(billRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageable))).thenReturn(page);
        // when
        Page<BillResponse> billResponses = billService.findAll(request);

        assertNotNull(billResponses);
        assertEquals(2, billResponses.getContent().size());
        assertEquals(2, billResponses.getContent().stream().findFirst().get().getBillDetails().size());

    }

    @Test
    void shouldReturnBillWhenFindById() {
        String id = "bill-id-1";
        Bill bill = Bill.builder()
                .id(id)
                .transDate(new Date())
                .transType(TransType.builder().id(EnumTransType.DI).build())
                .customer(Customer.builder().build())
                .table(MsTable.builder().build())
                .payment(Payment.builder().build())
                .billDetails(List.of(
                        BillDetail.builder().menu(Menu.builder().build()).build(),
                        BillDetail.builder().menu(Menu.builder().build()).build(),
                        BillDetail.builder().menu(Menu.builder().build()).build()
                ))
                .build();
        Optional<Bill> optionalBill = Optional.of(bill);
        Mockito.when(billRepository.findById(id)).thenReturn(optionalBill);

        BillResponse billResponse = billService.findById(id);

        assertNotNull(billResponse);
        assertEquals(3, billResponse.getBillDetails().size());
    }

    @Test
    void shouldReturnBillWhenUpdateStatus() {
        UpdateBillStatusRequest request = UpdateBillStatusRequest.builder()
                .orderId("order-id")
                .billStatus("settlement")
                .build();
        Bill bill = Bill.builder()
                .id(request.getOrderId())
                .transDate(new Date())
                .transType(TransType.builder().id(EnumTransType.DI).build())
                .customer(Customer.builder().build())
                .table(MsTable.builder().build())
                .payment(Payment.builder().billStatus("ordered").build())
                .billDetails(List.of(
                        BillDetail.builder()
                                .id("bill-dt-1")
                                .menu(Menu.builder()
                                        .name("menu-1")
                                        .price(3000L)
                                        .build())
                                .qty(3)
                                .build(),
                        BillDetail.builder()
                                .id("bill-dt-2")
                                .menu(Menu.builder()
                                        .name("menu-2")
                                        .price(3000L)
                                        .build())
                                .qty(3)
                                .build(),
                        BillDetail.builder()
                                .id("bill-dt-3")
                                .menu(Menu.builder()
                                        .name("menu-3")
                                        .price(3000L)
                                        .build())
                                .qty(2)
                                .build()
                ))
                .build();
        Optional<Bill> optionalBill = Optional.of(bill);
        Mockito.when(billRepository.findById(request.getOrderId())).thenReturn(optionalBill);
        Payment payment = bill.getPayment();
        payment.setBillStatus(request.getBillStatus());

        billService.updateStatus(request);

        Mockito.verify(billRepository, Mockito.times(1)).findById(request.getOrderId());
    }

    @Test
    void shouldReturnBillWhenFindAllBillToCsv() {
        BillRequest request = BillRequest.builder().build();
        List<Bill> bills = List.of(
                Bill.builder()
                        .customer(Customer.builder().build())
                        .table(MsTable.builder().build())
                        .transType(TransType.builder().id(EnumTransType.DI).build())
                        .transDate(new Date())
                        .payment(Payment.builder().build())
                        .billDetails(List.of(
                                BillDetail.builder().menu(Menu.builder().build()).build(),
                                BillDetail.builder().menu(Menu.builder().build()).build()
                        ))
                        .build(),
                Bill.builder()
                        .customer(Customer.builder().build())
                        .table(MsTable.builder().build())
                        .transType(TransType.builder().id(EnumTransType.DI).build())
                        .transDate(new Date())
                        .payment(Payment.builder().build())
                        .billDetails(List.of(
                                BillDetail.builder().menu(Menu.builder().build()).build(),
                                BillDetail.builder().menu(Menu.builder().build()).build()
                        ))
                        .build()
        );

        Mockito.when(billRepository.findAll(Mockito.any(Specification.class))).thenReturn(bills);

        // when
        List<CSVBillResponse> csvBillResponses = billService.findAllBillToCsv(request);

        // then
        assertNotNull(csvBillResponses);
    }
}