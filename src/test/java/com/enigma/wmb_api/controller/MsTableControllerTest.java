package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.MsTableRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.entity.MsTable;
import com.enigma.wmb_api.service.MsTableService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
class MsTableControllerTest {
    @MockBean
    private MsTableService tableService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave201StatusAndReturnCommonResponseWhenCreateTable() throws Exception {
        MsTableRequest request = MsTableRequest.builder().name("T01").build();
        MsTable table = MsTable.builder().name("T01").build();
        Mockito.when(tableService.create(Mockito.any())).thenReturn(table);

        String stringJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(APIUrl.TABLE_API)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(result -> {
                    CommonResponse<MsTable> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(201, response.getStatusCode());
                    assertEquals("T01", response.getData().getName());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void shouldHave200StatusAndReturnCommonResponseWhenUpdateTable() throws Exception {
        MsTable mockTable = MsTable.builder()
                .id("table-id")
                .name("T01").build();

        Mockito.when(tableService.update(Mockito.any(MsTable.class))).thenReturn(mockTable);

        String stringJson = objectMapper.writeValueAsString(mockTable);

        mockMvc.perform(
                        MockMvcRequestBuilders.put(APIUrl.TABLE_API)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<MsTable> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_UPDATE_DATA, response.getMessage());
                    assertEquals("T01", response.getData().getName());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void deleteTable() throws Exception {
        String id = "table-id";
        Mockito.doNothing().when(tableService).delete(id);
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(APIUrl.TABLE_API + "/" + id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<MsTable> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_DELETE_DATA, response.getMessage());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void findAllTable() throws Exception {
        List<MsTable> tables = List.of(
                MsTable.builder().build(),
                MsTable.builder().build()
        );
        Mockito.when(tableService.findAll()).thenReturn(tables);
        mockMvc.perform(
                        MockMvcRequestBuilders.get(APIUrl.TABLE_API)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<List<MsTable>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                    assertEquals(2, response.getData().size());
                });
    }

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void findTableById() throws Exception {
        String id = "table-id";
        MsTable mockTable = MsTable.builder()
                .id(id)
                .name("T01").build();
        Mockito.when(tableService.findById(id)).thenReturn(mockTable);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(APIUrl.TABLE_API + "/" + id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<MsTable> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                    assertEquals("T01", response.getData().getName());
                });

    }
}