package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = ConstantTable.TRANS_TYPE)
public class TransType {
    @Id
    private String id;

    @Column(name = "description")
    private String description;
}
