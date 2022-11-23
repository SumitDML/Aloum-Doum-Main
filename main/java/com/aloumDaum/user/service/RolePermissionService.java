package com.aloumDaum.user.service;

import org.springframework.data.domain.Pageable;

import com.aloumDaum.user.model.response.Data;

public interface RolePermissionService {
	
    Data fetchAll(Pageable pageable);
}
