package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.LoginResponse;
import com.enigma.wmb_api.dto.response.RegisterResponse;
import com.enigma.wmb_api.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = APIUrl.AUTH_API)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> registerUser(@RequestBody AuthRequest request) {
        RegisterResponse register = authService.register(request);
        CommonResponse<RegisterResponse> response = CommonResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(ResponseMessage.SUCCESS_SAVE_DATA)
                .data(register)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/register/admin",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> registerAdmin(@RequestBody AuthRequest request) {
        RegisterResponse register = authService.registerAdmin(request);
        CommonResponse<RegisterResponse> response = CommonResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(ResponseMessage.SUCCESS_SAVE_DATA)
                .data(register)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> login(@RequestBody AuthRequest request) {
        LoginResponse loginResponse = authService.login(request);
        CommonResponse<LoginResponse> response = CommonResponse.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_LOGIN)
                .data(loginResponse)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping(path = "validate-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateToken() {
        boolean valid = authService.validateToken();
        if (valid) {
            CommonResponse<String> response = CommonResponse.<String>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message(ResponseMessage.SUCCESS_GET_DATA)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            CommonResponse<String> response = CommonResponse.<String>builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message(ResponseMessage.ERROR_INVALID_JWT)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
