package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.MsTableRequest;
import com.enigma.wmb_api.entity.MsTable;
import com.enigma.wmb_api.repository.MsTableRepository;
import com.enigma.wmb_api.service.MsTableService;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MsTableServiceImpl implements MsTableService {
    private final MsTableRepository tableRepository;
    private final ValidationUtil validationUtil;

    @Override
    public List<MsTable> findAll() {
        return tableRepository.findAll();
    }

    @Override
    public MsTable findById(String id) {
        Optional<MsTable> table = tableRepository.findById(id);
        if (table.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "table not found");
        return table.get();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MsTable create(MsTableRequest request) {
        validationUtil.validate(request);
        MsTable table = MsTable.builder().name(request.getName()).build();
        return tableRepository.saveAndFlush(table);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MsTable update(MsTable menu) {
        validationUtil.validate(menu);
        findById(menu.getId());
        return tableRepository.saveAndFlush(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        MsTable table = findById(id);
        tableRepository.delete(table);
    }
}
