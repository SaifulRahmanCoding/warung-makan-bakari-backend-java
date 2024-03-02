package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.entity.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerSpecification {
    // isMember, mobilePhoneNo, name
    public static Specification<Customer> getSpecification(CustomerRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getName() != null) {
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%");
                predicates.add(name);

            }
            if (request.getMobilePhoneNo() != null) {
                Predicate phone = criteriaBuilder.equal(root.get("mobilePhoneNo"), request.getMobilePhoneNo());
                predicates.add(phone);
            }
            if (request.getIsMember() != null) {
                Predicate isMember = criteriaBuilder.equal(root.get("isMember"), request.getIsMember());
                predicates.add(isMember);
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
