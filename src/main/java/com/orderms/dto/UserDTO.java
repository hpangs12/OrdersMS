package com.orderms.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * DTO class for User
 * <p>
 * This DTO class ensures that the user is only getting what is needed and no sensitive data is exposed.
 * </p>
 *
 * <b>Sensitive data can include things like password, roles, etc. </b>
 */
@Data
@NoArgsConstructor
@ToString
public class UserDTO {
	
	private String username;
    private String password;
    private String emailId;
    private String firstName;
    private String lastName;
	private List<String> address;
	
}
