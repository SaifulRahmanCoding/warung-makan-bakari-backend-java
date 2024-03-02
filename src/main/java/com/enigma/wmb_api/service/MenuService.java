package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.entity.Menu;
import org.springframework.data.domain.Page;

public interface MenuService {
    Page<Menu> findAll(MenuRequest request);

    Menu findById(String id);

    Menu create(MenuRequest request);

    Menu update(Menu menu);

    void delete(String id);
}
