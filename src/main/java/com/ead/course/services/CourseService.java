package com.ead.course.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;

public interface CourseService {

	public void delete(CourseModel courseModel);

	public CourseModel save(CourseModel courseModel);

	public Optional<CourseModel> findById(UUID courseId);

	//public Page<CourseModel> findAll(SpecificationTemplate.CourseSpec spec, Pageable pageable);
	
	public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable);

	public boolean existsByCourseAndUser(UUID courseId, UUID userId);

	public void saveSubscriptionUserInCourse(UUID courseId, UUID userId);
	
	public void saveSubscriptionUserInCourseAndSendNotification(CourseModel courseModel, UserModel userModel);
	
}
