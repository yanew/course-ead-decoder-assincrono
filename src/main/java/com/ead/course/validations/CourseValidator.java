package com.ead.course.validations;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ead.course.dtos.CourseDto;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;

@Component
public class CourseValidator implements Validator{

	@Autowired
	@Qualifier("defaultValidator")
	private Validator validator;
	
	@Autowired
	private UserService userService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return false;
	}

	@Override
	public void validate(Object o, Errors errors) {
		CourseDto courseDto = (CourseDto) o;
		validator.validate(courseDto, errors);
		if(!errors.hasErrors()) {
			this.validateUserInstructor(courseDto.getUserInstructor(), errors);
		}
	}

	private void validateUserInstructor(UUID userInstructor, Errors errors) {
		Optional<UserModel> userModelOptional = userService.findById(userInstructor);
		
		if(userModelOptional.isPresent()) {
			if(userModelOptional.get().getUserType().equals(UserType.STUDENT.toString())) {
				errors.rejectValue("userInstructor", "UserInstructorError", "É necessário que o usuário seja INSTRUCTOR ou ADMIN para criar um curso!");
			}
		}else {
			errors.rejectValue("userInstructor", "UserInstructorError","Instrutor não encontrado!");
		}
	}
	
}
