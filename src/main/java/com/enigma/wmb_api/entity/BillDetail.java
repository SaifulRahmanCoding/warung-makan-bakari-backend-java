package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = ConstantTable.BILL_DETAIL)
public class BillDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonBackReference
    private Bill bill;

    @Column(name = "price", nullable = false, updatable = false)
    private Long price;

    @Column(name = "qty", nullable = false)
    private Float qty;
}
