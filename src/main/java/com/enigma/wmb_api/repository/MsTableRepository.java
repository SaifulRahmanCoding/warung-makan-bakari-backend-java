package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.MsTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsTableRepository extends JpaRepository<MsTable, String> {
}
