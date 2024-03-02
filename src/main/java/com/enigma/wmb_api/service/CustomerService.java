package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Page<CustomerResponse> findAll(CustomerRequest request);

    Customer findById(String id);

    Customer create(CustomerRequest request);

    Customer update(Customer customer);

    void delete(String id);

}
