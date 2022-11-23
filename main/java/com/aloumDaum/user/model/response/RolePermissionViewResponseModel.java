package com.aloumDaum.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionViewResponseModel {

    private Long totalElements;

    private Integer totalPages;

    private List<RolePermissionResponseModel> rolePermissionResponseModel;
}
