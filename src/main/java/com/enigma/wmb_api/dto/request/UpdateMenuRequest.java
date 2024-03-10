package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.entity.Image;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMenuRequest {
    private String id;
    private String name;
    private Long price;
    private MultipartFile image;
}
