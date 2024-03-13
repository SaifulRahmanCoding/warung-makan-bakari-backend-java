package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.dto.response.ImageResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MsTable;
import com.enigma.wmb_api.service.MenuService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class MenuControllerTest {
    @MockBean
    private MenuService menuService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @Test
    void deleteMenu() throws Exception {
        String id = "menu-id";
        Mockito.doNothing().when(menuService).delete(id);
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(APIUrl.MENU_API + "/" + id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<Menu> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_DELETE_DATA, response.getMessage());
                });
    }

    @Test
    void findAllMenu() throws Exception {
        MenuRequest request = MenuRequest.builder()
                .page(1)
                .size(10)
                .sortBy("name")
                .direction("asc")
                .build();
        List<MenuResponse> menuResponses = List.of(
                MenuResponse.builder().build(),
                MenuResponse.builder().build(),
                MenuResponse.builder().build()
        );
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Page<MenuResponse> responsePage = new PageImpl<>(menuResponses, pageable, menuResponses.size());
        Mockito.when(menuService.findAll(Mockito.any())).thenReturn(responsePage);
        mockMvc.perform(
                        MockMvcRequestBuilders.get(APIUrl.MENU_API)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<List<MenuResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getData());
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                    assertEquals(3, response.getData().size());
                });
    }

    @Test
    void findMenuById() throws Exception {
        String id = "menu-id";
        MenuResponse menuResponse = MenuResponse.builder()
                .image(ImageResponse.builder().build())
                .build();
        Mockito.when(menuService.findOneById(id)).thenReturn(menuResponse);
        mockMvc.perform(
                        MockMvcRequestBuilders.get(APIUrl.MENU_API + "/{id}", id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    CommonResponse<MenuResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getData().getImage());
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_GET_DATA, response.getMessage());
                });
    }
}