package com.aloumDaum.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionResponseModel {

    private Long id;
    private String pageName;
    private String pagePermission;

}
