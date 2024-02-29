package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerService {
    CustomerResponse create(CustomerRequest request);

    CustomerResponse findAll(CustomerRequest request);
}
