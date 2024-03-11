package com.enigma.wmb_api.dto.response;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CSVBillResponse {
    @CsvBindByPosition(position = 0)
    private String billId;

    @CsvBindByPosition(position = 1)
    private String transDate;

    @CsvBindByPosition(position = 2)
    private String customerName;

    @CsvBindByPosition(position = 3)
    private String tableName;

    @CsvBindByPosition(position = 4)
    private String transType;

    @CsvBindByPosition(position = 5)
    private String menuName;

    @CsvBindByPosition(position = 6)
    private String price;

    @CsvBindByPosition(position = 7)
    private String quantity;
}
