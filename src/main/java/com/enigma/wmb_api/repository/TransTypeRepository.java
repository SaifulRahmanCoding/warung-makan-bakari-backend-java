package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.constant.EnumTransType;
import com.enigma.wmb_api.entity.TransType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransTypeRepository extends JpaRepository<TransType, String> {
    @Query(value = "SELECT * FROM m_trans_type WHERE id = :transType", nativeQuery = true)
    Optional<TransType> getTransType(@Param("transType") String transType);
}
