package com.enigma.wmb_api.service;


import com.enigma.wmb_api.dto.request.MsTableRequest;
import com.enigma.wmb_api.entity.MsTable;

import java.util.List;

public interface MsTableService {
    List<MsTable> findAll();

    MsTable findById(String id);

    MsTable create(MsTableRequest request);

    MsTable update(MsTable table);

    void delete(String id);
}
