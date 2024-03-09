package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.UpdateCustomerRequest;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticateUserServiceImpl {
    private final CustomerService customerService;
    private final UserService userService;

    public boolean hasSameId(UpdateCustomerRequest request) {
        Customer currentCustomer = customerService.findByIdOrThrowNotFound(request.getId());
        UserAccount userAccount = userService.getByContext();

        if (!userAccount.getId().equals(currentCustomer.getUserAccount().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ResponseMessage.ERROR_FORBIDDEN);
        }
        return true;
    }
}
