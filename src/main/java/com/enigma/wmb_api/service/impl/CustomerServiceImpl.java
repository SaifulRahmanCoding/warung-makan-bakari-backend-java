package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.repository.CustomerRepository;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ValidationUtil validationUtil;

    @Override
    public CustomerResponse create(CustomerRequest request) {
        validationUtil.validate(request);
        Customer customer = Customer.builder()
                .name(request.getName())
                .mobilePhoneNo(request.getMobilePhoneNo())
                .isMember(request.getIsMember())
                .build();
        customerRepository.saveAndFlush(customer);

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .mobilePhoneNo(customer.getMobilePhoneNo())
                .isMember(customer.getIsMember())
                .build();
    }

    @Override
    public CustomerResponse findAll(CustomerRequest request) {
        return null;
    }
}
