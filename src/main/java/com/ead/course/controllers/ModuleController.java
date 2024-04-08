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

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

	@Autowired
	ModuleService moduleService;
	
	@Autowired
	CourseService courseService;
	
	@PostMapping("/courses/{courseId}/modules")
	public ResponseEntity<Object> saveModule(@PathVariable(value="courseId") UUID courseId,
													@RequestBody @Valid ModuleDto moduleDto){
		Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
		if(!courseModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não econtrado!");
		}
		var moduleModel = new ModuleModel();
		BeanUtils.copyProperties(moduleDto, moduleModel);
		moduleModel.setCourse(courseModelOptional.get());
		moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
		moduleModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(moduleModel));
	}
	
	@DeleteMapping("/courses/{courseId}/modules/{moduleId}")
	public ResponseEntity<Object> deleteModule(@PathVariable(value="courseId") UUID courseId,
											   @PathVariable(value="moduleId") UUID moduleId){
		Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
		if(!moduleModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Módulo não econtrado para o curso informado!");
		}
		moduleService.delete(moduleModelOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Módulo deletado com sucesso!");
	}
	
	@PutMapping("/courses/{courseId}/modules/{moduleId}")
	public ResponseEntity<Object> updateModule(@PathVariable(value="courseId") UUID courseId,
			   									@PathVariable(value="moduleId") UUID moduleId,
			   									@RequestBody @Valid  ModuleDto moduleDto){
		Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
		if(!moduleModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Módulo não econtrado para o curso informado!");
		}
		var moduleModel = moduleModelOptional.get();
		moduleModel.setTitle(moduleDto.getTitle());
		moduleModel.setDescription(moduleDto.getDescription());
		moduleModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(moduleModel));
	}
	
	//@GetMapping("/courses/{courseId}/modules")/****Esse método não funciona*********/
	/*public ResponseEntity<Object> getAllModulesBugado(@PathVariable(value="courseId") UUID courseId, SpecificationTemplate.ModuleSpec spec,
								@PageableDefault(page = 0, size = 1, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable){
		Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
		if(!courseModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
		}*//*else if(courseModelOptional.get().getModules().isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Este curso não possui módulos cadastrados!");
		}*///Esse else if deu pau. Entrou em loop infinito. Talvez pq a chamada é lazy? Não sei. 
		
		/*Page<ModuleModel> listaModulosCurso = 
				moduleService.findAllModulesIntoCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable);
		if(listaModulosCurso.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Este curso não possui módulos cadastrados!");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(listaModulosCurso);
	}*/
	
	@GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModules(@PathVariable(value="courseId") UUID courseId,
                                                           SpecificationTemplate.ModuleSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "moduleId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(
        		moduleService.findAllModulesIntoCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable));
    }
	
	@GetMapping("/courses/{courseId}/modules/{moduleId}")
	public ResponseEntity<Object> getOneModule(@PathVariable(value="courseId") UUID courseId,
												@PathVariable(value="moduleId") UUID moduleId){
		Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
		if(!moduleModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Módulo não econtrado para o curso informado!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
	}
	
}
