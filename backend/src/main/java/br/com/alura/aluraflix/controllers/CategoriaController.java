package br.com.alura.aluraflix.controllers;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.alura.aluraflix.dto.CategoryDTO;
import br.com.alura.aluraflix.services.CategoryService;

@RestController
@RequestMapping(value = "/categorias")
public class CategoriaController {

	@Autowired
	private CategoryService categoriaService;

	@GetMapping
	public ResponseEntity<List<CategoryDTO>> findAllPaged() {

		List<CategoryDTO> lista = this.categoriaService.findAllPaged();

		return ResponseEntity.ok(lista);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {

		CategoryDTO dto = this.categoriaService.findById(id);

		return ResponseEntity.ok().body(dto);
	}

	@GetMapping(value = "/{id}/videos")
	public ResponseEntity<CategoryDTO> getVideoByCategory(@PathVariable Long id) {

		CategoryDTO dto = this.categoriaService.getVideoByCategory(id);

		return ResponseEntity.ok().body(dto);
	}

	@PostMapping
	public ResponseEntity<CategoryDTO> save(@Valid @RequestBody CategoryDTO dto) {

		dto = this.categoriaService.save(dto);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				                             .path("/{id}")
				                             .buildAndExpand(dto.getId())
				                             .toUri();

		return ResponseEntity.created(uri).body(dto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {

		dto = this.categoriaService.update(id, dto);

		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {

		this.categoriaService.delete(id);

		return ResponseEntity.ok(new String("Categoria deletada com sucesso."));
	}
}