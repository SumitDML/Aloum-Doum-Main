package com.aloumDaum.user.service;

import com.aloumDaum.user.data.RoleEntity;
import com.aloumDaum.user.model.request.RoleRequestModel;
import com.aloumDaum.user.model.response.Data;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    public void createRole(RoleRequestModel roleDetails);
    public void updateRole(RoleRequestModel roleDetails);
    RoleEntity findById(Long roleEntityId);
    Data findAll(Pageable pageable);

}
