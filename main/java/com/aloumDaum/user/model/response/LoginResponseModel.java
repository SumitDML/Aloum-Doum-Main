package com.aloumDaum.user.model.response;


import com.aloumDaum.user.data.RolePermissionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseModel {

	private String firstName;
	private String lastName;
	private String authToken;
	private List<RolePermissionEntity> rolePermissions;
	private String roleName;

}
