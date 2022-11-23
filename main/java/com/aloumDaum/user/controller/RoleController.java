package com.aloumDaum.user.controller;

import com.aloumDaum.user.data.RoleEntity;
import com.aloumDaum.user.exception.ValidationException;
import com.aloumDaum.user.model.request.RoleRequestModel;
import com.aloumDaum.user.model.response.Data;
import com.aloumDaum.user.model.response.ResponseModel;
import com.aloumDaum.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/role")
public class RoleController {

    private static final String READ_WRITE = "hasPermission('manage_users', 'view & edit')";

    private static final String READ = "hasPermission('manage_users', 'view')";

    @Autowired
    private RoleService roleService;


    @PreAuthorize(READ)
    @GetMapping(value = "/getAll")
    public ResponseEntity<ResponseModel> findAll(final Pageable pageable) {
        final Data data = roleService.findAll(pageable);
        final ResponseModel responseModel = new ResponseModel(OK, "Operation completed successfully.", null,
                data);

        return new ResponseEntity<>(responseModel, OK);
    }

    @PreAuthorize(READ_WRITE)
    @PostMapping(path = "/create")
    public ResponseEntity<ResponseModel> create(@RequestBody final RoleRequestModel roleDetails) {
        ResponseModel responseModel = null;
        try {
            roleService.createRole(roleDetails);
            responseModel = new ResponseModel(OK, "Role created successfully.", null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
        }
        catch (ValidationException exception) {
            responseModel = new ResponseModel(1001  , exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize(READ_WRITE)
    @PostMapping(path = "/update")
    public ResponseEntity<ResponseModel> update(@RequestBody final RoleRequestModel roleDetails) {
        ResponseModel responseModel = null;
        try {
            roleService.updateRole(roleDetails);
            responseModel = new ResponseModel(OK, "Role updated successfully.", null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
        }
        catch (HttpClientErrorException.BadRequest exception) {
            responseModel = new ResponseModel(1001, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);

        }
    }
}
