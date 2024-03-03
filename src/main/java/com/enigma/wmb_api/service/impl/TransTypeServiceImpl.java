package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.EnumTransType;
import com.enigma.wmb_api.entity.TransType;
import com.enigma.wmb_api.repository.TransTypeRepository;
import com.enigma.wmb_api.service.TransTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransTypeServiceImpl implements TransTypeService {
    private final TransTypeRepository transTypeRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransType findOrSave(EnumTransType transType) {

        String description = (transType.name().equals("DI")) ? "Dine-in" : "Take Away";
        return transTypeRepository.getTransType(transType.name())
                .orElseGet(() -> transTypeRepository.saveAndFlush(TransType.builder().id(transType).description(description).build()));
    }
}
