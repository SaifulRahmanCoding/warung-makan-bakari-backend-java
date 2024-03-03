package com.enigma.wmb_api.service;

import com.enigma.wmb_api.constant.EnumTransType;
import com.enigma.wmb_api.entity.TransType;

public interface TransTypeService {
    TransType findOrSave(EnumTransType transType);
}
