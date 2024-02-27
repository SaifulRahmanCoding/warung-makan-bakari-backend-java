package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = ConstantTable.BILL)
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private MsTable table;

    @ManyToOne
    @JoinColumn(name = "trans_Type")
    private TransType transType;

    @Column(name = "trans_date", updatable = false)
    private Date transDate;

    @OneToMany(mappedBy = "bill")
    @JsonManagedReference
    private List<BillDetail> billDetails;
}
