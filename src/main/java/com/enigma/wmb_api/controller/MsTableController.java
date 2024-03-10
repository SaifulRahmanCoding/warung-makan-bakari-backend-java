package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.dto.request.MsTableRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.entity.MsTable;
import com.enigma.wmb_api.service.MsTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.TABLE_API)
public class MsTableController {
    private final MsTableService tableService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public ResponseEntity<CommonResponse<MsTable>> createTable(@RequestBody MsTableRequest request) {
        MsTable table = tableService.create(request);
        CommonResponse<MsTable> response = CommonResponse.<MsTable>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("successfully create new table")
                .data(table)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping
    public ResponseEntity<CommonResponse<MsTable>> updateTable(@RequestBody MsTable msTable) {
        MsTable table = tableService.update(msTable);
        CommonResponse<MsTable> response = CommonResponse.<MsTable>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully update table")
                .data(table)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<MsTable>> deleteTable(@PathVariable String id) {
        tableService.delete(id);
        CommonResponse<MsTable> response = CommonResponse.<MsTable>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully delete table")
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponse<List<MsTable>>> findAllTable() {
        List<MsTable> tables = tableService.findAll();
        CommonResponse<List<MsTable>> response = CommonResponse.<List<MsTable>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get all table")
                .data(tables)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<MsTable>> findTableById(@PathVariable String id) {
        MsTable table = tableService.findById(id);
        CommonResponse<MsTable> response = CommonResponse.<MsTable>builder()
                .statusCode(HttpStatus.OK.value())
                .message("successfully get table")
                .data(table)
                .build();
        return ResponseEntity.ok(response);
    }
}
