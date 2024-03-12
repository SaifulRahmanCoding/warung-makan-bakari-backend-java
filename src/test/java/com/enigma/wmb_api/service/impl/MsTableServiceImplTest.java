package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.MsTableRequest;
import com.enigma.wmb_api.entity.MsTable;
import com.enigma.wmb_api.repository.MsTableRepository;
import com.enigma.wmb_api.service.MsTableService;
import com.enigma.wmb_api.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MsTableServiceImplTest {

    @Mock
    private MsTableRepository tableRepository;
    @Mock
    private ValidationUtil validationUtil;
    private MsTableService tableService;

    @BeforeEach
    void setUp() {
        tableService = new MsTableServiceImpl(tableRepository, validationUtil);
    }

    @Test
    void shouldReturnTablesWhenFindAll() {
        // given
        // stubbing
        List<MsTable> tables = List.of(
                MsTable.builder().id("table-id-1").name("T01").build(),
                MsTable.builder().id("table-id-2").name("T02").build(),
                MsTable.builder().id("table-id-3").name("T03").build()
        );
        // stubbing config
        Mockito.when(tableRepository.findAll()).thenReturn(tables);

        // when
        List<MsTable> actualTables = tableService.findAll();
        // then
        assertNotNull(actualTables);
        assertEquals(3, actualTables.size());
    }

    @Test
    void shouldReturnTableWhenFindById() {
        // given
        String id = "table-id-1";
        // stubbing
        MsTable table = MsTable.builder().id(id).name("T01").build();
        // stubbing config
        Mockito.when(tableRepository.findById(id)).thenReturn(Optional.of(table));
        // when
        MsTable actualTable = tableService.findById(id);
        // then
        assertNotNull(actualTable);
        assertEquals("T01", actualTable.getName());
    }

    @Test
    void shouldReturnTableWhenCreate() {
        // given
        MsTableRequest request = MsTableRequest.builder()
                .name("T01")
                .build();
        // stubbing
        MsTable table = MsTable.builder().id("table-id-1").name(request.getName()).build();
        // stubbing config
        Mockito.doNothing().when(validationUtil).validate(request);
        Mockito.when(tableRepository.saveAndFlush(Mockito.any())).thenReturn(table);

        // when
        MsTable actualTable = tableService.create(request);
        // then
        assertNotNull(actualTable);
        assertEquals("T01", actualTable.getName());

    }

    @Test
    void shouldReturnTableWhenUpdate() {
        // given
        MsTable table = MsTable.builder().id("table-id-1").name("T01").build();
        // stubbing
        MsTable currentTable = MsTable.builder().id(table.getId()).name("T05").build();
        // stubbing config
        Mockito.doNothing().when(validationUtil).validate(table);
        Mockito.when(tableRepository.findById(table.getId())).thenReturn(Optional.of(currentTable));
        Mockito.when(tableRepository.saveAndFlush(table)).thenReturn(table);

        // when
        MsTable actualTable = tableService.update(table);
        // then
        assertNotNull(actualTable);
        assertEquals("T01", actualTable.getName());
    }

    @Test
    void shouldDeleteSuccessfully() {
        // given
        String id = "table-id-1";

        // stubbing
        MsTable table = MsTable.builder().id(id).name("T01").build();

        Optional<MsTable> optionalTable = Optional.of(table);
        // stubbing config
        Mockito.when(tableRepository.findById(id)).thenReturn(optionalTable);
        Mockito.doNothing().when(tableRepository).delete(table);
        // when
        tableService.delete(id);

        // then
        Mockito.verify(tableRepository, Mockito.times(1)).delete(optionalTable.get());
    }
}