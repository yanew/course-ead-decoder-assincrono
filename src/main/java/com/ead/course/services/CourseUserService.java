package com.ead.course.services;

import java.util.UUID;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;

public interface CourseUserService {

	public boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId);

	public CourseUserModel save(CourseUserModel convertCourseUserModel);

	public CourseUserModel saveAndSendSubscriptionUserInCourse(CourseUserModel courseUserModel);

	public boolean existsByCourseId(UUID userId);

	public void deleteCourseUserByUser(UUID userId);

}
