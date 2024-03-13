package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.request.UpdateCustomerRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.service.CustomerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class CustomerControllerTest {
    @MockBean
    private CustomerService customerService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave200StatusAndReturnCommandResponseWhenUpdateCustomer() throws Exception {
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .id("cs-1")
                .name("cs-name")
                .build();
        CustomerResponse customerResponse = CustomerResponse.builder()
                .id(request.getId())
                .name(request.getName())
                .build();
        Mockito.when(customerService.update(Mockito.any())).thenReturn(customerResponse);

        String stringJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        MockMvcRequestBuilders.put(APIUrl.CUSTOMER_API)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<CustomerResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_UPDATE_DATA, response.getMessage());
                    assertEquals("cs-name", response.getData().getName());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave200StatusAndReturnCommandResponseWhenDeleteCustomer() throws Exception {
        String id = "cs-1";
        Mockito.doNothing().when(customerService).delete(id);
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(APIUrl.CUSTOMER_API + "/{id}", id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_DELETE_DATA, response.getMessage());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave200StatusAndReturnCommandResponseWhenFindCustomerById() throws Exception {
        String id = "cs-1";
        CustomerResponse customerResponse = CustomerResponse.builder().id(id).build();
        Mockito.when(customerService.findOneById(Mockito.any())).thenReturn(customerResponse);
        mockMvc.perform(
                        MockMvcRequestBuilders.get(APIUrl.CUSTOMER_API + "/{id}", id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<CustomerResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getData());
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave200StatusAndReturnCommandResponseWhenFindAllCustomer() throws Exception {
        CustomerRequest request = CustomerRequest.builder()
                .page(1)
                .size(10)
                .sortBy("name")
                .direction("asc")
                .name("mawar")
                .build();
        List<CustomerResponse> customerResponses = List.of(
                CustomerResponse.builder()
                        .id("cs-id-1")
                        .name(request.getName())
                        .build()
        );
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Page<CustomerResponse> responsePage = new PageImpl<>(customerResponses, pageable, customerResponses.size());
        Mockito.when(customerService.findAll(Mockito.any())).thenReturn(responsePage);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(APIUrl.CUSTOMER_API)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<List<CustomerResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                    assertEquals("mawar",response.getData().stream().findFirst().get().getName());
                });
    }
}