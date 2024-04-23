package com.ead.course.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ead.course.dtos.NotificationCommandDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.models.UserModel;
import com.ead.course.publishers.NotificationCommanderPublisher;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.CourseService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CourseServiceImpl implements CourseService{

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	ModuleRepository moduleRepository;

	@Autowired
	LessonRepository lessonRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	NotificationCommanderPublisher notificationCommanderPublisher;
	
	/**
	 * Modo mais performático (maior desempenho) de deleção, sem delegar ao JPA nem ao Banco de dados.
	 */
	@Transactional
	@Override
	public void delete(CourseModel courseModel) {
		boolean deleteCourseUserInAuthUser = false;
		List<ModuleModel> moduleModelList = this.moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());
		if(!moduleModelList.isEmpty()) {
			for (ModuleModel moduleModel : moduleModelList) {
				List<LessonModel> lessonModelList = this.lessonRepository.findAllLessonsIntoModule(moduleModel.getModuleId());
				if(!lessonModelList.isEmpty()) {
					this.lessonRepository.deleteAll(lessonModelList);	
				}
			}
			this.moduleRepository.deleteAll(moduleModelList);
		}

		this.courseRepository.deleteCourseUserByCourse(courseModel.getCourseId());
		
		this.courseRepository.delete(courseModel);
	}

	@Override
	public CourseModel save(CourseModel courseModel) {
		return courseRepository.save(courseModel);
	}

	@Override
	public Optional<CourseModel> findById(UUID courseId) {
		return courseRepository.findById(courseId);
	}

	@Override
	public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
		return courseRepository.findAll(spec, pageable);
	}

	@Override
	public boolean existsByCourseAndUser(UUID courseId, UUID userId) {
		return courseRepository.existsByCourseAndUser(courseId, userId);
	}

	@Transactional
	@Override
	public void saveSubscriptionUserInCourse(UUID courseId, UUID userId) {
		courseRepository.saveCourseUser(courseId, userId);
	}
	
	@Transactional
	@Override
	public void saveSubscriptionUserInCourseAndSendNotification(CourseModel courseModel, UserModel userModel) {
		courseRepository.saveCourseUser(courseModel.getCourseId(), userModel.getUserId());
		try {
			var notificationCommandDto = new NotificationCommandDto();
			notificationCommandDto.setTitle("Bem vindo(a) ao Curso: " + courseModel.getName());
			notificationCommandDto.setMessage(userModel.getFullName() + " a sua inscrição foi realizada com sucesso!");
			notificationCommandDto.setUserId(userModel.getUserId());
			notificationCommanderPublisher.publishNotificationCommand(notificationCommandDto);
		} catch(Exception e) {
			log.warn("Erro ao enviar a notificação!");
		}
	}
	
}
