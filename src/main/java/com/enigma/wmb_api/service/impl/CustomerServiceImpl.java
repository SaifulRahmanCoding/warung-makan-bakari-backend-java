package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.request.UpdateCustomerRequest;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Customer create(Customer customer) {
        return customerRepository.saveAndFlush(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse update(UpdateCustomerRequest request) {
        Customer currentCustomer = findByIdOrThrowNotFound(request.getId());

        currentCustomer.setName(request.getName());
        currentCustomer.setMobilePhoneNo(request.getMobilePhoneNo());
        customerRepository.saveAndFlush(currentCustomer);
        return convertCustomerToCustomerResponse(currentCustomer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        Customer customer = findByIdOrThrowNotFound(id);
        customerRepository.delete(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatusMemberById(String id, Boolean isMember) {
        findByIdOrThrowNotFound(id);
        customerRepository.updateStatus(id, isMember);
    }

    @Transactional(readOnly = true)
    @Override
    public Customer findByIdOrThrowNotFound(String id) {
        return customerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerResponse> findAll(CustomerRequest request) {
        // cek apakah page kurang dari 0, jika ya maka set sama dengan 1
        if (request.getPage() <= 0) request.setPage(1);
        // buat sort by dan direction nya
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        // buat page
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        // buat specification untuk query custom-nya
        Specification<Customer> specification = CustomerSpecification.getSpecification(request);
        // find all customer dan jadikan tipe data page
        Page<Customer> customers = customerRepository.findAll(specification, pageable);

        // log.info("size content: " + customers.getContent().size());
        // latihan untuk membuat response bill
        // buat list customer response dengan value content customers yang di ubah menggunakan stream map
        List<CustomerResponse> customerResponses = customers.getContent().stream()
                .map(customer -> {
                    return CustomerResponse.builder()
                            .id(customer.getId())
                            .name(customer.getName())
                            .mobilePhoneNo(customer.getMobilePhoneNo())
                            .isMember(customer.getIsMember())
                            .userAccountId(customer.getUserAccount().getId())
                            .build();
                }).toList();
        return new PageImpl<>(customerResponses, pageable, customers.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse findOneById(String id) {
        return convertCustomerToCustomerResponse(findByIdOrThrowNotFound(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Customer findById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    private CustomerResponse convertCustomerToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .mobilePhoneNo(customer.getMobilePhoneNo())
                .isMember(customer.getIsMember())
                .userAccountId(customer.getUserAccount().getId())
                .build();
    }
}