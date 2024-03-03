package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.BillRequest;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.util.DateUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BillSpecification {
    public static Specification<Bill> getSpecification(BillRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getTransDate() != null) {
                Date date = DateUtil.parseDate(request.getTransDate(), "yyyy-MM-dd");
                Timestamp endDate = new Timestamp(date.getTime() + 86399000);
                // log.info("tanggal-> " + date + " " + endDate);
                Predicate transDate = criteriaBuilder.between(root.get("transDate"), date, endDate);
                predicates.add(transDate);
            }

            if (request.getStartDate() != null || request.getEndDate() != null) {

                if (request.getStartDate() != null && request.getEndDate() != null) {

                    Date startDate = DateUtil.parseDate(request.getStartDate(), "yyyy-MM-dd");
                    Date endDate = DateUtil.parseDate(request.getEndDate(), "yyyy-MM-dd");

                    // log.info(startDate + " " + endDate);
                    Predicate rangeDate = criteriaBuilder.between(root.get("transDate"), startDate, new Timestamp(endDate.getTime() + 86399000));
                    predicates.add(rangeDate);

                } else if (request.getStartDate() != null) {

                    Date date = DateUtil.parseDate(request.getStartDate(), "yyyy-MM-dd");
                    log.info(date + " \n");
                    Predicate startDate = criteriaBuilder.greaterThanOrEqualTo(root.get("transDate"), date);
                    log.info(startDate + " \n");

                    predicates.add(startDate);

                } else {

                    Date date = DateUtil.parseDate(request.getEndDate(), "yyyy-MM-dd");
                    log.info(date + " ");
                    Predicate startDate = criteriaBuilder.greaterThanOrEqualTo(root.get("transDate"), date);
                    predicates.add(startDate);
                }
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
