package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import com.enigma.wmb_api.constant.EnumTransType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.TRANS_TYPE)
public class TransType {
    @Id
    @Enumerated(EnumType.STRING)
    private EnumTransType id;

    @Column(name = "description")
    private String description;
}
