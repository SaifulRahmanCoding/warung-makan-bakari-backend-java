package com.enigma.wmb_api.service.impl;


import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.request.UpdateMenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Image;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repository.MenuRepository;
import com.enigma.wmb_api.service.ImageService;
import com.enigma.wmb_api.service.MenuService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private ImageService imageService;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuServiceImpl(menuRepository, imageService);
    }

    @Test
    void shouldReturnPageOfMenuWhenFindAll() {
        // given
        MenuRequest request = MenuRequest.builder()
                .maxPrice(3000L)
                .minPrice(2000L)
                .page(2)
                .size(3)
                .sortBy("name")
                .direction("asc")
                .build();
        // stubbing
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Image image = Image.builder().build();
        List<Menu> menus = List.of(
                Menu.builder()
                        .id("menu-id-1")
                        .name("menu-1")
                        .price(2500L)
                        .image(image)
                        .build(),
                Menu.builder()
                        .id("menu-id-2")
                        .name("menu-2")
                        .price(2000L)
                        .image(image)
                        .build()
        );

        Page<Menu> page = new PageImpl<>(menus);
        // stubbing config
        Mockito.when(menuRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageable))).thenReturn(page);
        // when
        Page<MenuResponse> menuResponses = menuService.findAll(request);
        // then
        assertNotNull(menuResponses);
        assertEquals("menu-1", menuResponses.getContent().stream().findFirst().get().getName());
    }

    @Test
    void shouldReturnMenuWhenFindById() {
        // given
        String id = "menu-id-1";
        // stubbing
        Menu menu = Menu.builder()
                .id(id)
                .name("menu-1")
                .price(2500L)
                .image(Image.builder().build())
                .build();
        // stubbing config
        Mockito.when(menuRepository.findById(id)).thenReturn(Optional.of(menu));
        // when
        Menu actualMenu = menuService.findById(id);
        // then
        assertNotNull(actualMenu);
    }

    @Test
    void shouldReturnMenuWhenFindOneById() {
        String id = "menu-id-1";
        // stubbing
        Menu menu = Menu.builder()
                .id(id)
                .name("menu-1")
                .price(2500L)
                .image(Image.builder().build())
                .build();
        // stubbing config
        Mockito.when(menuRepository.findById(id)).thenReturn(Optional.of(menu));
        // when
        MenuResponse menuResponse = menuService.findOneById(id);
        // then
        assertNotNull(menuResponse);
        assertEquals("menu-1", menuResponse.getName());
    }

    @Test
    void shouldReturnMenuWhenCreate() {
        // given
        byte[] content = "test content".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile mockImage = new MockMultipartFile("image", "test.jpg", "image/jpg", content);

        MenuRequest request = MenuRequest.builder()
                .image(mockImage)
                .name("menu-1")
                .price(2500L)
                .build();

        // stubbing
        Image image = Image.builder().build();
        Menu menu = Menu.builder()
                .id("menu-id-1")
                .name(request.getName())
                .price(request.getPrice())
                .image(image)
                .build();

        // stubbing config
        Mockito.when(imageService.create(request.getImage())).thenReturn(image);
        Mockito.when(menuRepository.saveAndFlush(Mockito.any())).thenReturn(menu);

        // when
        MenuResponse menuResponse = menuService.create(request);

        // then
        assertNotNull(menuResponse);
        assertEquals("menu-1", menuResponse.getName());
    }

    @Test
    void shouldReturnMenuWhenUpdate() {
        byte[] content = "test content".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile mockImage = new MockMultipartFile("image", "test.jpg", "image/jpg", content);

        UpdateMenuRequest request = UpdateMenuRequest.builder()
                .id("menu-id-1")
                .name("menu")
                .image(mockImage)
                .price(3000L)
                .build();
        // stubbing
        Image currentImage = Image.builder().id("image-id").build();
        Image newImage = Image.builder().id("new-image-id").build();
        Menu menu = Menu.builder()
                .id(request.getId())
                .name("menu-1")
                .price(2500L)
                .image(currentImage)
                .build();

        Menu newMenu = Menu.builder()
                .id(request.getId())
                .name(request.getName())
                .price(request.getPrice())
                .image(newImage).build();
        // stubbing config
        Mockito.when(menuRepository.findById(request.getId())).thenReturn(Optional.of(menu));
        Mockito.when(imageService.create(request.getImage())).thenReturn(newImage);
        Mockito.when(menuRepository.saveAndFlush(Mockito.any())).thenReturn(newMenu);
        Mockito.doNothing().when(imageService).delete(currentImage.getId());

        // when
        MenuResponse response = menuService.update(request);
        // then
        assertNotNull(response);
        Assertions.assertEquals("menu", response.getName());
        Assertions.assertEquals(3000L, response.getPrice());
    }

    @Test
    void shouldDeleteSuccessfully() {
        // given
        String id = "menu-id-1";
        // stubbing
        Menu menu = Menu.builder()
                .id(id)
                .name("menu-1")
                .price(2500L)
                .image(Image.builder().build())
                .build();
        // stubbing config
        Mockito.when(menuRepository.findById(id)).thenReturn(Optional.of(menu));
        Mockito.doNothing().when(menuRepository).delete(menu);

        // when
        menuService.delete(id);
        // then
        Mockito.verify(menuRepository, Mockito.times(1)).delete(menu);
    }
}