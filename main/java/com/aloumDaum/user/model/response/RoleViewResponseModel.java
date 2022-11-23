package com.aloumDaum.user.model.response;

import java.util.List;

public class RoleViewResponseModel {
    private Long totalElements;

    private Integer totalPages;

    private List<RoleResponseModel> roleResponseModel;

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<RoleResponseModel> getRoleResponseModel() {
        return roleResponseModel;
    }

    public void setRoleResponseModel(List<RoleResponseModel> roleResponseModel) {
        this.roleResponseModel = roleResponseModel;
    }
}
