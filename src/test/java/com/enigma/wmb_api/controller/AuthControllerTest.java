package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.dto.response.LoginResponse;
import com.enigma.wmb_api.dto.response.RegisterResponse;
import com.enigma.wmb_api.service.AuthService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class AuthControllerTest {
    @MockBean
    private AuthService authService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHave201StatusAndReturnCommandResponseWhenRegisterUser() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("kamada")
                .password("123")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .username(request.getUsername())
                .roles(List.of("ROLE_CUSTOMER"))
                .build();
        Mockito.when(authService.register(Mockito.any())).thenReturn(registerResponse);
        String stringJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        MockMvcRequestBuilders.post(APIUrl.AUTH_API + "/register")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<RegisterResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(201, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_SAVE_DATA, response.getMessage());
                    assertEquals(registerResponse.getUsername(), response.getData().getUsername());
                });
    }

    @Test
    void shouldHave201StatusAndReturnCommandResponseWhenRegisterAdmin() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("kamada.admin")
                .password("123")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .username(request.getUsername())
                .roles(List.of("ROLE_CUSTOMER", "ROLE_ADMIN"))
                .build();
        Mockito.when(authService.registerAdmin(Mockito.any())).thenReturn(registerResponse);
        String stringJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        MockMvcRequestBuilders.post(APIUrl.AUTH_API + "/register/admin")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<RegisterResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(201, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_SAVE_DATA, response.getMessage());
                    assertEquals(registerResponse.getUsername(), response.getData().getUsername());
                    assertEquals(2, response.getData().getRoles().size());
                });
    }

    @Test
    void shouldHave200StatusAndReturnCommandResponseWhenLogin() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("kamada.admin")
                .password("123")
                .build();
        LoginResponse loginResponse = LoginResponse.builder()
                .username(request.getUsername())
                .token("rtyuio765456tyuijhwfd37")
                .roles(List.of("ROLE_CUSTOMER", "ROLE_ADMIN"))
                .build();
        Mockito.when(authService.login(Mockito.any())).thenReturn(loginResponse);
        String stringJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        MockMvcRequestBuilders.post(APIUrl.AUTH_API + "/login")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(stringJson)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(result -> {
                    CommonResponse<LoginResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(200, response.getStatusCode());
                    assertEquals(ResponseMessage.SUCCESS_LOGIN, response.getMessage());
                    assertEquals(loginResponse.getUsername(), response.getData().getUsername());
                    assertNotNull(response.getData().getToken());
                    assertEquals(2, response.getData().getRoles().size());
                });
    }
}