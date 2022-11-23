package com.aloumDaum.user.repository;

import com.aloumDaum.user.data.RolePermissionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends CrudRepository<RolePermissionEntity, Long>, PagingAndSortingRepository<RolePermissionEntity, Long>{

	RolePermissionEntity findByPageNameAndPagePermission(String pageName, String pagePermission);

}
