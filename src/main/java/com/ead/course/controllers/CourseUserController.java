package com.ead.course.controllers;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {
	
	@Autowired
	AuthUserClient authuserClient;
	
	@Autowired
	CourseService courseService;
	
	@Autowired
	CourseUserService courseUserService;
	
	@GetMapping("/courses/{courseId}/users")
	public ResponseEntity<Object> getAllUsersByCourse(
			@PageableDefault(page = 0, size = 1, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
			@PathVariable(value = "courseId") UUID courseId){
		Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
		if(!courseModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(authuserClient.getAllUsersByCourse(courseId, pageable));
	}
	
	@PostMapping("/courses/{courseId}/users/subscription")
	public ResponseEntity<Object> saveSubscriptionUsetInCourse(@PathVariable(value = "courseId") UUID courseId,
																@RequestBody @Valid SubscriptionDto subscriptionDto){
		Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
		if(!courseModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
		}
		
		if(courseUserService.existsByCourseAndUserId(courseModelOptional.get(),subscriptionDto.getUserId())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já cadastrado no curso!");
		}
		
		try {
		ResponseEntity<UserDto> responseUserDto = authuserClient.getOneUserById(subscriptionDto.getUserId());
		if(responseUserDto.getBody().getUserStatus().equals(UserStatus.BLOCKED)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Este usuário está bloqueado!");
		}
		} catch(HttpStatusCodeException e) {
			if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");	
			}
		}
		
		CourseUserModel courseUserModel = 
				courseUserService.saveAndSendSubscriptionUserInCourse(courseModelOptional.get().convertCourseUserModel(subscriptionDto.getUserId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);
	}
	
	@DeleteMapping("/courses/users/{userId}")
	public ResponseEntity<Object> deleteUserCourseByCourse(@PathVariable(value = "userId") UUID userId){
		if(!courseUserService.existsByCourseId(userId)){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CourseUser não encontrado!");
		}
		courseUserService.deleteCourseUserByUser(userId);
		return ResponseEntity.status(HttpStatus.OK).body("CourseUser criado com sucesso!");
	}

}
