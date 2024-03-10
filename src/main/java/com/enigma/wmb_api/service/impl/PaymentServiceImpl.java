package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.PaymentDetailRequest;
import com.enigma.wmb_api.dto.request.PaymentItemDetailRequest;
import com.enigma.wmb_api.dto.request.PaymentRequest;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.Payment;
import com.enigma.wmb_api.repository.PaymentRepository;
import com.enigma.wmb_api.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RestClient restClient;
    private final String SECRET_KEY;
    private final String BASE_URL_SNAP;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository, RestClient restClient,
            @Value("${midtrans.api.key}") String secretKey,
            @Value("${midtrans.api.snap_url}") String baseUrlSnap
    ) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
        SECRET_KEY = secretKey;
        BASE_URL_SNAP = baseUrlSnap;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Payment createPayment(Bill bill) {
        long amount = bill.getBillDetails().stream().mapToLong(
                value -> (value.getQty() * value.getPrice())).reduce(0, Long::sum);

        List<PaymentItemDetailRequest> itemDetailRequestsList = bill.getBillDetails().stream().map(billDetail ->
                        PaymentItemDetailRequest.builder()
                                .name(billDetail.getMenu().getName())
                                .price(billDetail.getPrice())
                                .quantity(billDetail.getQty())
                                .build())
                .toList();

        PaymentRequest request = PaymentRequest.builder().paymentDetails(PaymentDetailRequest.builder()
                        .orderId(bill.getId())
                        .amount(amount)
                        .build())
                .paymentItemDetails(itemDetailRequestsList)
                .paymentMethod(List.of("bri_va", "bni_va", "gopay", "shopeepay"))
                .build();

        ResponseEntity<Map<String, String>> response = restClient.post()
                .uri(BASE_URL_SNAP)
                .body(request)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + SECRET_KEY).retrieve().toEntity(new ParameterizedTypeReference<>() {
                });

        Map<String, String> body = response.getBody();

        assert body != null;
        Payment payment = Payment.builder()
                .token(body.get("token"))
                .redirectUrl(body.get("redirect_url"))
                .billStatus("ordered")
                .build();
        paymentRepository.saveAndFlush(payment);

        return payment;
    }
}
