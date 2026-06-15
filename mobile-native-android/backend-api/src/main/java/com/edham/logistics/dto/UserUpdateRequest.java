package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private Long organizationId;
    private String status;
    private Set<String> roles;
}
