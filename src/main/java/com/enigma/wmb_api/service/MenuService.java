package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Menu;
import org.springframework.data.domain.Page;

public interface MenuService {
    Page<MenuResponse> findAll(MenuRequest request);

    Menu findById(String id);
    MenuResponse findOneById(String id);

    MenuResponse create(MenuRequest request);

    MenuResponse update(UpdateMenuRequest menu);

    void delete(String id);
}
