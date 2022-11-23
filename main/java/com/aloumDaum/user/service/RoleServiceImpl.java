package com.aloumDaum.user.service;

import com.aloumDaum.user.data.RoleEntity;
import com.aloumDaum.user.data.RolePermissionEntity;
import com.aloumDaum.user.exception.ValidationException;
import com.aloumDaum.user.model.request.RoleRequestModel;
import com.aloumDaum.user.model.response.Data;
import com.aloumDaum.user.model.response.RoleResponseModel;
import com.aloumDaum.user.model.response.RoleViewResponseModel;
import com.aloumDaum.user.repository.RolePermissionRepository;
import com.aloumDaum.user.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private transient RoleRepository roleRepo;

    @Autowired
    private transient RolePermissionRepository rolePermissionRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public void createRole(RoleRequestModel roleDetails) {
        RoleEntity roleEntity = roleRepo.findByRoleName(roleDetails.getRoleName());
        if (roleEntity != null) {
            throw new ValidationException("Role with name " + roleDetails.getRoleName() + " already exists.");
        }
        final ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        roleEntity = mapper.map(roleDetails, RoleEntity.class);
        roleRepo.save(roleEntity);
    }

    @Override
    public void updateRole(RoleRequestModel roleDetails) {
        RoleEntity roleEntity = roleRepo.findById(roleDetails.getId()).orElse(null);
        if (roleEntity != null) {
            RoleEntity roleCheck = roleRepo.findByRoleName(roleDetails.getRoleName());
            if (roleCheck == null || roleEntity.getRoleName().equals(roleDetails.getRoleName()))
            {
                roleEntity.setRoleName(roleDetails.getRoleName());
                roleEntity.setDescription(roleDetails.getDescription());
                Set<RolePermissionEntity> obj = roleDetails.getPermissions().stream().map(a -> {
                    RolePermissionEntity r = new RolePermissionEntity();
                    RolePermissionEntity permissionCheck = rolePermissionRepo.findById(a.getId()).orElse(null);
                    if(permissionCheck!=null)
                    {
                        r.setId(a.getId());
                    }
                    else
                        throw new ValidationException("Permission does not exist");
                    return r;
                }).collect(Collectors.toSet());
                roleEntity.setPermissions(obj);
                  try {
                    roleRepo.save(roleEntity);
                } catch (ValidationException exception) {
                    exception.printStackTrace();
                }
            } else
                throw new ValidationException("Role Name already exists");
        } else {
            throw new EntityNotFoundException("Role with id " + roleDetails.getId() + " does not exist.");
        }
    }

    @Override
    public Data findAll(Pageable pageable) {
        Data data = new Data();
        final RoleViewResponseModel roleViewResponseModel = new RoleViewResponseModel();
        final List<RoleResponseModel> roleResponseModels = new ArrayList<>();
        final Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createdAt").descending());
        final Page<RoleEntity> findRoles = roleRepo.findAll(page);
        if (findRoles.hasContent()) {
            roleViewResponseModel.setTotalElements(findRoles.getTotalElements());
            roleViewResponseModel.setTotalPages(findRoles.getTotalPages());
            findRoles.getContent().forEach(role -> {
                roleResponseModels.add(prepareRoleViewResponse(role));
            });
            roleViewResponseModel.setRoleResponseModel(roleResponseModels);
        }
        data.setData(roleViewResponseModel);
        return data;
    }

    @Override
    public RoleEntity findById(Long roleEntityId) {
        final Optional<RoleEntity> roleEntity = roleRepo.findById(roleEntityId);
        if (roleEntity.isPresent()) {
            return roleEntity.get();
        }
        throw new EntityNotFoundException("Role with id " + roleEntityId + " does not exist.");
    }


    private RoleResponseModel prepareRoleViewResponse(final RoleEntity entity) {
        RoleResponseModel response = mapper.map(entity, RoleResponseModel.class);
        return response;

    }
}
