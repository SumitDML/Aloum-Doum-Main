
package com.aloumDaum.user.model.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestModel {

	@NotBlank(message = "First name cannot be null")
	@Size(min = 2, message = "First name must not be less than 2 characters")
	private String firstName;

	@NotBlank(message = "Last name cannot be null")
	private String lastName;

	@NotBlank(message = "Email cannot be null")
	@Email(message = "Invalid email")
	private String email;

	private RoleRequestModel roles;
}

