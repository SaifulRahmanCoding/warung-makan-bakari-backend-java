package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {
    @Modifying
    @Query(value = "UPDATE m_customer SET is_member = :isMember WHERE id = :id", nativeQuery = true)
    void updateStatus(@Param("id") String id, @Param("isMember") Boolean isMember);
}
