package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.request.UpdateCustomerRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = APIUrl.CUSTOMER_API)
public class CustomerController {
    private final CustomerService customerService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN') OR @authenticateUserServiceImpl.hasSameId(#request)")
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<CustomerResponse>> updateCustomer(@RequestBody UpdateCustomerRequest request) {
        CustomerResponse newCustomer = customerService.update(request);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_UPDATE_DATA)
                .data(newCustomer)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<String>> deleteCustomer(@PathVariable String id) {
        customerService.delete(id);
        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_DELETE_DATA)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<CustomerResponse>> findCustomerById(@PathVariable String id) {
        CustomerResponse customer = customerService.findOneById(id);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(customer)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
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
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(customers.getContent())
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

}
