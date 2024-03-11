package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.Payment;
import com.enigma.wmb_api.util.DateUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class CSVBillSpecification {
    public static Specification<Bill> getSpecification(BillRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getStartDate() != null || request.getEndDate() != null) {

                if (request.getStartDate() != null && request.getEndDate() != null) {

                    Date startDate = DateUtil.parseDate(request.getStartDate(), "yyyy-MM-dd");
                    Date endDate = DateUtil.parseDate(request.getEndDate(), "yyyy-MM-dd");

                    // log.info(startDate + " " + endDate);
                    Predicate rangeDate = criteriaBuilder.between(root.get("transDate"), startDate, new Timestamp(endDate.getTime() + 86399000));
                    predicates.add(rangeDate);

                } else if (request.getStartDate() != null) {

                    Date date = DateUtil.parseDate(request.getStartDate(), "yyyy-MM-dd");
                    Predicate startDate = criteriaBuilder.greaterThanOrEqualTo(root.get("transDate"), date);

                    predicates.add(startDate);

                } else {

                    Date date = DateUtil.parseDate(request.getEndDate(), "yyyy-MM-dd");
                    Predicate startDate = criteriaBuilder.greaterThanOrEqualTo(root.get("transDate"), date);
                    predicates.add(startDate);
                }
            }
            Join<Bill, Payment> billJoin = root.join("payment", JoinType.INNER);
            Predicate billStatusPredicate = criteriaBuilder.equal(billJoin.get("billStatus"), "settlement");
            predicates.add(billStatusPredicate);

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
