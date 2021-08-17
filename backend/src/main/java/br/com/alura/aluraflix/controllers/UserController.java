package br.com.alura.aluraflix.controllers;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.alura.aluraflix.dto.UserDTO;
import br.com.alura.aluraflix.dto.UserInsertDTO;
import br.com.alura.aluraflix.dto.UserUpdateDTO;
import br.com.alura.aluraflix.services.UserService;

@RestController
@RequestMapping(value = "/usuarios")
public class UserController {

	private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAllPaged(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
			@RequestParam(value = "orderBy", defaultValue = "nome") String orderBy) {

		StringBuffer params = new StringBuffer();
		params.append(page).append("\n");
		params.append(linesPerPage).append("\n");
		params.append(direction).append("\n");
		params.append(orderBy).append("\n");

		LOGGER.info("START METHOD UserController.findAllPaged: {} {} {} {} " + params);

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);

		List<UserDTO> emptyList = Collections.emptyList();

		Page<UserDTO> list = new PageImpl<>(emptyList, pageRequest, emptyList.size());

		try {			
			list = this.userService.findAllPaged(pageRequest);
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo UserController.findAll " + e);
		}

		LOGGER.info("END METHOD UserController.findAllPaged");

		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(Long id) {

		LOGGER.info("START METHOD UserController.findAllPaged: {} " + id);

		UserDTO dto = new UserDTO();

		try {
		  dto = this.userService.findById(id);
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo UserController.findById " + e);
		}
		
		LOGGER.info("END METHOD UserController.findAllPaged");

		return ResponseEntity.ok().body(dto);
	}

	@PostMapping
	public ResponseEntity<UserDTO> save(@Valid @RequestBody UserInsertDTO dto) {

		LOGGER.info("START METHOD UserController.save");

		UserDTO newdto = new UserDTO();

		try {
		  newdto = this.userService.save(dto);
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo UserController.save " + e);
		}

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newdto.getId()).toUri();

		LOGGER.info("END METHOD UserController.save");

		return ResponseEntity.created(uri).body(newdto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {

		LOGGER.info("START METHOD UserController.update");

		UserDTO updateDto = new UserDTO();

		try {
		  updateDto = this.userService.update(id, dto);
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo UserController.update " + e);
		}

		LOGGER.info("END METHOD UserController.update");

		return ResponseEntity.ok().body(updateDto);
	}
}