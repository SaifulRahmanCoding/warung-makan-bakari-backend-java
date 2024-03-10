package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.dto.response.PagingResponse;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.service.MenuService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.MENU_API)
public class MenuController {
    private final MenuService menuService;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> createMenu(
            @RequestPart(name = "menu") String jsonMenu,
            @RequestPart(name = "image") MultipartFile image
    ) {
        CommonResponse.CommonResponseBuilder<MenuResponse> responseBuilder = CommonResponse.builder();
        try {
            MenuRequest request = objectMapper.readValue(jsonMenu, new TypeReference<>() {
            });
            request.setImage(image);
            MenuResponse menuResponse = menuService.create(request);
            responseBuilder.statusCode(HttpStatus.CREATED.value());
            responseBuilder.message(ResponseMessage.SUCCESS_SAVE_DATA);
            responseBuilder.data(menuResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBuilder.build());
        } catch (Exception e) {
            responseBuilder.message(ResponseMessage.ERROR_INTERNAL_SERVER);
            responseBuilder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBuilder.build());
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<?>> updateMenu(
            @RequestPart(name = "menu") String jsonMenu,
            @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        CommonResponse.CommonResponseBuilder<MenuResponse> responseBuilder = CommonResponse.builder();
        try {
            UpdateMenuRequest request = objectMapper.readValue(jsonMenu, new TypeReference<>() {
            });
            request.setImage(image);
            MenuResponse menuResponse = menuService.update(request);
            responseBuilder.statusCode(HttpStatus.OK.value());
            responseBuilder.message(ResponseMessage.SUCCESS_UPDATE_DATA);
            responseBuilder.data(menuResponse);
            return ResponseEntity.status(HttpStatus.OK).body(responseBuilder.build());

        } catch (Exception e) {
            responseBuilder.message(ResponseMessage.ERROR_INTERNAL_SERVER);
            responseBuilder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBuilder.build());
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<Menu>> deleteMenu(@PathVariable String id) {
        menuService.delete(id);
        CommonResponse<Menu> response = CommonResponse.<Menu>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_DELETE_DATA)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<MenuResponse>>> findAllMenu(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "price", required = false) Long price,
            @RequestParam(name = "minPrice", required = false) Long minPrice,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice
    ) {
        // buat request untuk parameter method findAll
        MenuRequest request = MenuRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .name(name)
                .price(price)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
        // jadikan tipe data menjadi page
        Page<MenuResponse> menus = menuService.findAll(request);
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(menus.getTotalPages())
                .totalElement(menus.getTotalElements())
                .page(menus.getPageable().getPageNumber() + 1)
                .hasNext(menus.hasNext())
                .hasPrevious(menus.hasPrevious())
                .size(menus.getSize())
                .build();
        CommonResponse<List<MenuResponse>> response = CommonResponse.<List<MenuResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(menus.getContent())
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<MenuResponse>> findMenuById(@PathVariable String id) {
        MenuResponse menu = menuService.findOneById(id);
        CommonResponse<MenuResponse> response = CommonResponse.<MenuResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(menu)
                .build();
        return ResponseEntity.ok(response);
    }
}
