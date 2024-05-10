package com.enigma.wmb_api.dto.response;

import com.enigma.wmb_api.entity.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private String id;
    private String name;
    private String mobilePhoneNo;
    private Boolean isMember;
    private String userAccountId;
    private String username;
    private String role;
}
