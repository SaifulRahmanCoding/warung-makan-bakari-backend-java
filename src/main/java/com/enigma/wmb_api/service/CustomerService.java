package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.request.UpdateCustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Page<CustomerResponse> findAll(CustomerRequest request);

    CustomerResponse findOneById(String id);

    Customer findById(String id);

    Customer create(Customer customer);

    CustomerResponse update(UpdateCustomerRequest request);

    void delete(String id);

    void updateStatusMemberById(String id, Boolean status);

    Customer findByIdOrThrowNotFound(String id);

}
