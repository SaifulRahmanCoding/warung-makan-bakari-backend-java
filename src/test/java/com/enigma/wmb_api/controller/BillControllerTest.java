package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.dto.response.BillResponse;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.service.BillService;
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
class BillControllerTest {
    @MockBean
    private BillService billService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave201StatusAndReturnCommandResponseWhenCreateBill() throws Exception {
        BillRequest request = BillRequest.builder().customerId("cs-id").build();
        BillResponse billResponse = BillResponse.builder().customerId(request.getCustomerId()).build();
        Mockito.when(billService.create(Mockito.any())).thenReturn(billResponse);
        String stringJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        MockMvcRequestBuilders.post(APIUrl.BILL_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<BillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getData());
                    assertEquals(201, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_SAVE_DATA, response.getMessage());
                    assertEquals(billResponse.getCustomerId(), response.getData().getCustomerId());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave200StatusAndReturnCommandResponseWhenFindAllBill() throws Exception {
        BillRequest request = BillRequest.builder()
                .page(1)
                .size(10)
                .sortBy("name")
                .direction("asc")
                .build();
        List<BillResponse> billResponses = List.of(
                BillResponse.builder().build(),
                BillResponse.builder().build(),
                BillResponse.builder().build()
        );
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Page<BillResponse> responsePage = new PageImpl<>(billResponses, pageable, billResponses.size());
        Mockito.when(billService.findAll(Mockito.any())).thenReturn(responsePage);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(APIUrl.BILL_API)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<List<BillResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getData());
                    assertEquals(3, response.getData().size());
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave200StatusAndReturnCommandResponseWhenFindBillById() throws Exception {
        String id = "bill-id";
        BillResponse billResponse = BillResponse.builder().id(id).build();
        Mockito.when(billService.findById(Mockito.any())).thenReturn(billResponse);
        mockMvc.
                perform(
                        MockMvcRequestBuilders.get(APIUrl.BILL_API + "/{id}", id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<BillResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getData());
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                });

    }
}