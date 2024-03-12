package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.request.UpdateCustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repository.CustomerRepository;
import com.enigma.wmb_api.service.CustomerService;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ValidationUtil validationUtil;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, validationUtil);
    }

    @Test
    void shouldReturnCustomerWhenCreate() {
        Customer customer = Customer.builder().build();
        Mockito.when(customerRepository.saveAndFlush(Mockito.any())).thenReturn(customer);
        Customer actualCustomer = customerService.create(customer);
        assertNotNull(actualCustomer);
    }

    @Test
    void shouldReturnCustomerWhenUpdate() {
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .id("cs-1")
                .name("cuy")
                .mobilePhoneNo("5678909")
                .build();

        Customer customer = Customer.builder()
                .userAccount(UserAccount.builder().build())
                .build();

        Customer newCustomer = Customer.builder()
                .id(request.getId())
                .name(request.getName())
                .mobilePhoneNo(request.getMobilePhoneNo())
                .userAccount(UserAccount.builder().build())
                .build();
        Mockito.when(customerRepository.findById(request.getId())).thenReturn(Optional.of(customer));
        Mockito.when(customerRepository.saveAndFlush(Mockito.any())).thenReturn(newCustomer);

        CustomerResponse response = customerService.update(request);
        assertNotNull(response);
        assertEquals("cuy", response.getName());
    }

    @Test
    void shouldDeleteSuccessfully() {
        String id = "cs-1";
        Customer customer = Customer.builder()
                .userAccount(UserAccount.builder().build())
                .build();
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        Mockito.doNothing().when(customerRepository).delete(customer);

        customerService.delete(id);
        Mockito.verify(customerRepository, Mockito.times(1)).delete(customer);
    }

    @Test
    void shouldReturnCustomerWhenUpdateStatusMemberById() {
        String id = "cs-1";
        Boolean status = true;
        Customer customer = Customer.builder()
                .userAccount(UserAccount.builder().build())
                .build();
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        Mockito.doNothing().when(customerRepository).updateStatus(id, status);

        customerService.updateStatusMemberById(id, status);
        Mockito.verify(customerRepository, Mockito.times(1)).updateStatus(id, status);

    }

    @Test
    void shouldReturnCustomersWhenFindAll() {
        CustomerRequest request = CustomerRequest.builder()
                .page(2)
                .size(3)
                .sortBy("name")
                .direction("asc")
                .build();
        List<Customer> customers = List.of(
                Customer.builder().userAccount(UserAccount.builder().build()).build(),
                Customer.builder().userAccount(UserAccount.builder().build()).build(),
                Customer.builder().userAccount(UserAccount.builder().build()).build()
        );

        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Page<Customer> page = new PageImpl<>(customers);

        Mockito.when(customerRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageable))).thenReturn(page);
        // when
        Page<CustomerResponse> customerResponses = customerService.findAll(request);

        assertNotNull(customerResponses);
        assertEquals(3, customerResponses.getSize());
    }

    @Test
    void shouldReturnCustomerWhenFindOneById() {
        String id = "cs-1";
        Customer customer = Customer.builder()
                .userAccount(UserAccount.builder().build())
                .build();
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        CustomerResponse response = customerService.findOneById(id);
        assertNotNull(response);
    }

    @Test
    void shouldReturnCustomerWhenFindById() {
        String id = "cs-1";
        Customer customer = Customer.builder()
                .userAccount(UserAccount.builder().build())
                .build();
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        Customer actualCustomer = customerService.findById(id);
        assertNotNull(actualCustomer);

    }
}