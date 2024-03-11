package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api.dto.response.ImageResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repository.MenuRepository;
import com.enigma.wmb_api.service.ImageService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.specification.MenuSpecification;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    @Override
    public Page<MenuResponse> findAll(MenuRequest request) {
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            if (request.getMinPrice() > request.getMaxPrice())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.INVALID_MIN_MAX_PRICE);
        }
        if (request.getPage() <= 0) request.setPage(1);
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Specification<Menu> specification = MenuSpecification.getSpecification(request);
        Page<Menu> menus = menuRepository.findAll(specification, pageable);

        List<MenuResponse> menuResponses = menus.getContent().stream().map(this::convertMenuToMenuResponse).toList();
        return new PageImpl<>(menuResponses, pageable, menus.getTotalElements());
    }

    @Override
    public Menu findById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    @Transactional(readOnly = true)
    @Override
    public MenuResponse findOneById(String id) {
        Menu menu = findByIdOrThrowNotFound(id);
        return convertMenuToMenuResponse(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuResponse create(MenuRequest request) {
        if (request.getImage().isEmpty())
            throw new ConstraintViolationException("image is required", null);
        Image image = imageService.create(request.getImage());
        Menu menu = Menu.builder()
                .name(request.getName())
                .price(request.getPrice())
                .image(image)
                .build();
        menuRepository.saveAndFlush(menu);
        return convertMenuToMenuResponse(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuResponse update(UpdateMenuRequest request) {
        Menu currentMenu = findByIdOrThrowNotFound(request.getId());
        currentMenu.setName(request.getName());
        currentMenu.setPrice(request.getPrice());
        String oldImageId = currentMenu.getImage().getId();
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            Image image = imageService.create(request.getImage());
            currentMenu.setImage(image);

            imageService.delete(oldImageId);
        }
        menuRepository.saveAndFlush(currentMenu);
        return convertMenuToMenuResponse(currentMenu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        Menu menu = findByIdOrThrowNotFound(id);
        String imageId = menu.getImage().getId();
        menuRepository.delete(menu);
        imageService.delete(imageId);
    }

    private Menu findByIdOrThrowNotFound(String id) {
        return menuRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.ERROR_NOT_FOUND));
    }

    private MenuResponse convertMenuToMenuResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .image(ImageResponse.builder()
                        .url(APIUrl.PRODUCT_IMAGE_DOWNLOAD_API + menu.getImage().getId())
                        .name(menu.getImage().getName())
                        .build())
                .build();
    }
}
