package com.aloumDaum.user.service;

import com.aloumDaum.user.data.RolePermissionEntity;
import com.aloumDaum.user.model.response.Data;
import com.aloumDaum.user.model.response.RolePermissionResponseModel;
import com.aloumDaum.user.model.response.RolePermissionViewResponseModel;
import com.aloumDaum.user.repository.RolePermissionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionRepository permissionRepository;

    @Override
    public Data fetchAll(Pageable pageable) {
        Data data = new Data();
        final RolePermissionViewResponseModel permissionViewResponseModel = new RolePermissionViewResponseModel();
        final List<RolePermissionResponseModel> rolePermissionResponseModels = new ArrayList<>();
        final Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createdAt").descending());
        final Page<RolePermissionEntity> findPermissions = permissionRepository.findAll(page);
        if (findPermissions.hasContent()) {
            permissionViewResponseModel.setTotalElements(findPermissions.getTotalElements());
            permissionViewResponseModel.setTotalPages(findPermissions.getTotalPages());

            findPermissions.getContent().forEach(permission -> {
                rolePermissionResponseModels.add(prepareRoleViewResponse(permission));
            });
            permissionViewResponseModel.setRolePermissionResponseModel(rolePermissionResponseModels);
        }
        data.setData(permissionViewResponseModel);
        return data;
    }

    private RolePermissionResponseModel prepareRoleViewResponse(final RolePermissionEntity entity) {
        final ModelMapper mapper = new ModelMapper();
        RolePermissionResponseModel response = mapper.map(entity, RolePermissionResponseModel.class);

        return response;

    }

}

