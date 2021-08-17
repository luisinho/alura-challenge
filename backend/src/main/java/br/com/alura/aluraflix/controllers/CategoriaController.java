package br.com.alura.aluraflix.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.alura.aluraflix.dto.CategoryDTO;
import br.com.alura.aluraflix.services.CategoryService;

@RestController
@RequestMapping(value = "/categorias")
public class CategoriaController {

	private static Logger LOGGER = LoggerFactory.getLogger(CategoriaController.class);

	@Autowired
	private CategoryService categoriaService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping
	public ResponseEntity<Page<CategoryDTO>> findAllPaged(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "5") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
			@RequestParam(value = "orderBy", defaultValue = "titulo") String orderBy) {

		StringBuffer params = new StringBuffer();
		params.append(page).append("\n");
		params.append(linesPerPage).append("\n");
		params.append(direction).append("\n");
		params.append(orderBy).append("\n");

		LOGGER.info("START METHOD CategoriaController.findAllPaged: {} {} {} {} " + params);

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction),  orderBy);

		Page<CategoryDTO> list = this.categoriaService.findAllPaged(pageRequest);

		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {

		LOGGER.info("START METHOD CategoriaController.findById: {} " + id);

		CategoryDTO  dto = this.categoriaService.findById(id);		

		LOGGER.info("END METHOD CategoriaController.findById");

		return ResponseEntity.ok().body(dto);
	}

	@GetMapping(value = "/{id}/videos")
	public ResponseEntity<CategoryDTO> getVideoByCategory(@PathVariable Long id) {

		LOGGER.info("START METHOD CategoriaController.getVideoByCategory: {} " + id);

		CategoryDTO dto = this.categoriaService.getVideoByCategory(id);

		LOGGER.info("END METHOD CategoriaController.getVideoByCategory");

		return ResponseEntity.ok().body(dto);
	}

	@PostMapping
	public ResponseEntity<CategoryDTO> save(@Valid @RequestBody CategoryDTO dto) {

		LOGGER.info("START METHOD CategoriaController.save: {} " + dto.toString());

		dto = this.categoriaService.save(dto);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				                             .path("/{id}")
				                             .buildAndExpand(dto.getId())
				                             .toUri();

		LOGGER.info("END METHOD CategoriaController.save");

		return ResponseEntity.created(uri).body(dto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {

		LOGGER.info("START METHOD CategoriaController.update: {} {} " + id + " - " + dto.toString());

		dto = this.categoriaService.update(id, dto);

		LOGGER.info("START METHOD CategoriaController.update");

		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {

		LOGGER.info("START METHOD CategoriaController.delete: {} " + id);

		this.categoriaService.delete(id);

		LOGGER.info("END METHOD CategoriaController.delete");

		return ResponseEntity.ok(new String(this.messageSource.getMessage("category.deleting.success", null, null)));
	}
}