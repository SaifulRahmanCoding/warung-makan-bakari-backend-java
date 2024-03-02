package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MsTableRequest {
    @NotBlank
    private String name;
}
