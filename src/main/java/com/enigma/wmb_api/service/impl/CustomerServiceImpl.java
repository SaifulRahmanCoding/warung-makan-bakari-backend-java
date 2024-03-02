package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.repository.CustomerRepository;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.specification.CustomerSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ValidationUtil validationUtil;

    @Override
    public Customer create(CustomerRequest request) {
        validationUtil.validate(request);
        Customer customer = Customer.builder()
                .name(request.getName())
                .mobilePhoneNo(request.getMobilePhoneNo())
                .isMember(request.getIsMember())
                .build();
        return customerRepository.saveAndFlush(customer);
    }

    @Override
    public Customer update(Customer customer) {
        validationUtil.validate(customer);
        findById(customer.getId());
        return customerRepository.saveAndFlush(customer);
    }

    @Override
    public void delete(String id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
    }

    @Override
    public Page<CustomerResponse> findAll(CustomerRequest request) {
        if (request.getPage() <= 0) request.setPage(1);
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Specification<Customer> specification = CustomerSpecification.getSpecification(request);
        Page<Customer> customers = customerRepository.findAll(specification, pageable);

        log.info("size content: " + customers.getContent().size());
        List<CustomerResponse> customerResponses = customers.getContent().stream()
                .map(customer -> {
                    return CustomerResponse.builder()
                            .id(customer.getId())
                            .name(customer.getName())
                            .mobilePhoneNo(customer.getMobilePhoneNo())
                            .isMember(customer.getIsMember())
                            .build();
                }).toList();
        return new PageImpl<>(customerResponses, pageable, customers.getTotalElements());
    }

    @Override
    public Customer findById(String id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found");
        return optionalCustomer.get();
    }
}