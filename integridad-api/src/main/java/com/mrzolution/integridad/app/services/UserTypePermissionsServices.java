package com.mrzolution.integridad.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.UserType;
import com.mrzolution.integridad.app.domain.UserTypePermissions;
import com.mrzolution.integridad.app.repositories.UserTypePermissionsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserTypePermissionsServices {
	
	@Autowired
	UserTypePermissionsRepository userTypePermissionsRepository;
	
	public UserTypePermissions create(UserTypePermissions userTypePermissions) {
		return userTypePermissionsRepository.save(userTypePermissions);
	}
	
	public Iterable<UserTypePermissions> findByUserType(UserType userType) {
		Iterable<UserTypePermissions> userTypePermissions = userTypePermissionsRepository.findByUserType(userType);
		for (UserTypePermissions userTypePermission : userTypePermissions) {
//			userTypePermission.setFatherListToNull();
			userTypePermission.setUserType(null);
		}
		return userTypePermissions;
	}
	
	
	
	private void populateChildren(UserTypePermissions userTypePermisions) {
		log.info("UserTypePermissionsServices populateChildren userTypePermisionsId: {}", userTypePermisions.getId());
		
		userTypePermisions.setFatherListToNull();
		log.info("UserTypeServices populateChildren FINISHED userTypeId: {}", userTypePermisions.getId());
	}

}
