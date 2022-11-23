package com.aloumDaum.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseModel {

    private Long id;
    private String roleName;
    private String description;
    private Set<RolePermissionResponseModel> permissions;
}
