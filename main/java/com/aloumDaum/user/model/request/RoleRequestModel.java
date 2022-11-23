package com.aloumDaum.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestModel {

    private Long id;

    private String roleName;

    private String description;

    private Set<RolePermissionRequestModel> permissions;

}
