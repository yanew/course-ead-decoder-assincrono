package com.ead.course.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

	@Autowired
	LessonService lessonService;
	
	@Autowired
	ModuleService moduleService;
	
	@PostMapping("/modules/{moduleId}/lessons")
	public ResponseEntity<Object> saveController(@PathVariable(value="moduleId") UUID moduleId,
													@RequestBody @Valid LessonDto lessonDto){
		Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
		if(!moduleModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Módulo não econtrado!");
		}
		var lessonModel = new LessonModel();
		BeanUtils.copyProperties(lessonDto, lessonModel);
		lessonModel.setModule(moduleModelOptional.get());
		lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
		lessonModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(lessonModel));
	}
	
	@DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
	public ResponseEntity<Object> deleteLesson(@PathVariable(value="moduleId") UUID moduleId,
											   @PathVariable(value="lessonId") UUID lessonId){
		Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
		if(!lessonModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lição não econtrada para o módulo informado!");
		}
		lessonService.delete(lessonModelOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Lição deletada com sucesso!");
	}
	
	@PutMapping("/modules/{moduleId}/lessons/{lessonId}")
	public ResponseEntity<Object> updateLesson(@PathVariable(value="moduleId") UUID moduleId,
												@PathVariable(value="lessonId") UUID lessonId,
			   									@RequestBody @Valid  LessonDto lessonDto){
		Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
		if(!lessonModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lição não econtrada para o módulo informado!");
		}
		var lessonModel = lessonModelOptional.get();
		lessonModel.setTitle(lessonDto.getTitle());
		lessonModel.setDescription(lessonDto.getDescription());
		lessonModel.setVideoUrl(lessonDto.getVideoUrl());
		lessonModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(lessonModel));
	}
	
	//@GetMapping("/modules/{moduleId}/lessons")
	/*public ResponseEntity<Object> getAllLessonsBugado(@PathVariable(value="moduleId") UUID moduleId, SpecificationTemplate.LessonSpec spec,
			@PageableDefault(page = 0, size = 1, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable){
		Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
		if(!moduleModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Módulo não encontrado!");
		}
		
		List<LessonModel> listaLessonModulo = lessonService.findAllLessonsIntoModule(moduleId);
		if(listaLessonModulo.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Este módulo não possui lições cadastradas!");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(listaLessonModulo);
	}*/
	
	@GetMapping("/modules/{moduleId}/lessons")
	public ResponseEntity<Page<LessonModel>> getAllLesson(@PathVariable(value="moduleId") UUID moduleId, SpecificationTemplate.LessonSpec spec,
			@PageableDefault(page = 0, size = 1, sort = "lessonId", direction = Sort.Direction.ASC) Pageable pageable){
		return ResponseEntity.status(HttpStatus.OK).body(
				lessonService.findAllLessonsIntoModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable));
	}
	
	@GetMapping("/modules/{moduleId}/lessons/{lessonId}")
	public ResponseEntity<Object> getOneLesson(@PathVariable(value="moduleId") UUID moduleId,
												@PathVariable(value="lessonId") UUID lessonId){
		Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
		if(!lessonModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lição não econtrada para o módulo informado!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(lessonModelOptional.get());
	}
	
}
