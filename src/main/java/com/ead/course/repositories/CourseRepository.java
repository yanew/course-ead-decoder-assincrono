package com.ead.course.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ead.course.models.CourseModel;

public interface CourseRepository extends JpaRepository<CourseModel, UUID>, JpaSpecificationExecutor<CourseModel>{

	
	@Query(value= "select case when count(tcu)>0 THEN true ELSE false END FROM tb_courses_users tcu WHERE tcu.course_id = :courseId and tcu.user_id = :userId", nativeQuery = true)
	public boolean existsByCourseAndUser(@Param("courseId") UUID courseId, @Param("userId") UUID userId);

	@Modifying
	@Query(value= "insert into tb_courses_users values (:courseId, :userId)", nativeQuery = true)
	public void saveCourseUser(@Param("courseId") UUID courseId, @Param("userId") UUID userId);
	
	@Modifying
	@Query(value= "delete from tb_courses_users where course_id = :courseId", nativeQuery = true)
	public void deleteCourseUserByCourse(@Param("courseId") UUID courseId);
	
	@Modifying
	@Query(value= "delete from tb_courses_users where user_id = :userId", nativeQuery = true)
	public void deleteCourseUserByUser(@Param("userId") UUID userId);
	
}