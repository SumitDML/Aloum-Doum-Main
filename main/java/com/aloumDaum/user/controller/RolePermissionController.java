package com.aloumDaum.user.controller;

import com.aloumDaum.user.model.response.Data;
import com.aloumDaum.user.model.response.ResponseModel;
import com.aloumDaum.user.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/permission")
public class RolePermissionController {

    private static final String READ_WRITE = "hasPermission('manage_users', 'view & edit')";
    private static final String READ = "hasPermission('manage_users', 'view')";

    @Autowired
    private RolePermissionService rolePermissionService;

    @PreAuthorize(READ)
    @GetMapping(value = "/getAll")
    public ResponseEntity<ResponseModel> findAll(final Pageable pageable) {
        final Data data = rolePermissionService.fetchAll(pageable);
        final ResponseModel responseModel = new ResponseModel(OK, "Operation completed successfully.", null,
                data);

        return new ResponseEntity<>(responseModel, OK);
    }
}
