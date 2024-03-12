package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.EnumTransType;
import com.enigma.wmb_api.entity.TransType;
import com.enigma.wmb_api.repository.TransTypeRepository;
import com.enigma.wmb_api.service.TransTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TransTypeServiceImplTest {
    @Mock
    private TransTypeRepository transTypeRepository;
    private TransTypeService transTypeService;

    @BeforeEach
    void setUp() {
        transTypeService = new TransTypeServiceImpl(transTypeRepository);
    }

    @Test
    void shouldReturnTransTypeWhenFindOrSave() {
        // given
        EnumTransType userTransType = EnumTransType.TA;
        // stubbing
        TransType transType = TransType.builder().id(userTransType).build();
        Optional<TransType> optionalTransType = Optional.of(transType);
        // stubbing config
        Mockito.when(transTypeRepository.getTransType(transType.getId().name())).thenReturn(optionalTransType);
        // when
        TransType actualTransType = transTypeService.findOrSave(userTransType);

        // then
        assertNotNull(actualTransType);
        assertEquals("TA",actualTransType.getId().name());
    }
}