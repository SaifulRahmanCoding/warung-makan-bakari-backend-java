package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = APIUrl.CUSTOMER_API)
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CommonResponse<Customer>> createNewCustomer(@RequestBody CustomerRequest request) {
        Customer customer = customerService.create(request);
        CommonResponse<Customer> response = CommonResponse.<Customer>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("successfully create new customer")
                .data(customer)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<CommonResponse<Customer>> updateCustomer(@RequestBody Customer customer) {
        Customer newCustomer = customerService.update(customer);
        CommonResponse<Customer> response = CommonResponse.<Customer>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully update customer")
                .data(newCustomer)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<Customer>> deleteCustomer(@PathVariable String id) {
        customerService.delete(id);
        CommonResponse<Customer> response = CommonResponse.<Customer>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully delete customer")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<Customer>> findCustomerById(@PathVariable String id) {
        Customer customer = customerService.findById(id);
        CommonResponse<Customer> response = CommonResponse.<Customer>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get customer")
                .data(customer)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CustomerResponse>>> findAllCustomer(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "member", required = false) Boolean member
    ) {
        CustomerRequest request = CustomerRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .name(name)
                .mobilePhoneNo(phone)
                .isMember(member)
                .build();
        Page<CustomerResponse> customers = customerService.findAll(request);
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(customers.getTotalPages())
                .totalElement(customers.getTotalElements())
                .page(customers.getPageable().getPageNumber() + 1)
                .size(customers.getPageable().getPageSize())
                .hasNext(customers.hasNext())
                .hasPrevious(customers.hasPrevious())
                .build();

        CommonResponse<List<CustomerResponse>> response = CommonResponse.<List<CustomerResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success get all customer")
                .data(customers.getContent())
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

}
