package com.ead.course.validations;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ead.course.dtos.CourseDto;

@Component
public class CourseValidator implements Validator{

	@Autowired
	@Qualifier("defaultValidator")
	private Validator validator;
	
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
		/*ResponseEntity<UserDto> responseUserInstructor;
		try {
			responseUserInstructor = authUserClient.getOneUserById(userInstructor);
			if(responseUserInstructor.getBody().getUserType().equals(UserType.STUDENT)) {
				errors.rejectValue("userInstructor", "UserInstructorError", "É necessário que o usuário seja INSTRUCTOR ou ADMIN para criar um curso!");
			}
		}catch(HttpStatusCodeException e) {
			if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				errors.rejectValue("userInstructor", "UserInstructorError","Instrutor não encontrado!");
			}
		}*/
	}
	
}
