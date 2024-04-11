package com.ead.course.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.ead.course.models.UserModel;

public interface UserService {

	public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable);

	public UserModel save(UserModel userModel);

	public void delete(UUID userId);


}
