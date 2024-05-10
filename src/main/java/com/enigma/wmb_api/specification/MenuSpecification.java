package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.entity.Menu;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MenuSpecification {
    public static Specification<Menu> getSpecification(MenuRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getName() != null) {
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%");
                predicates.add(name);
            }
            if (request.getPrice() != null) {
                Predicate price = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getPrice());
                predicates.add(price);
            }
            if (request.getMinPrice() != null || request.getMaxPrice() != null) {
                if (request.getMinPrice() != null && request.getMaxPrice() != null) {
                    Predicate range = criteriaBuilder.between(root.get("price"), request.getMinPrice(), request.getMaxPrice());
                    predicates.add(range);
                } else if (request.getMinPrice() != null) {
                    Predicate startPrice = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice());
                    predicates.add(startPrice);
                } else {
                    Predicate startPrice = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMaxPrice());
                    predicates.add(startPrice);
                }
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
