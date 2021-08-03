package br.com.alura.alurafix.controllers;

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

import br.com.alura.alurafix.dto.CategoriaDTO;
import br.com.alura.alurafix.services.CategoriaService;

@RestController
@RequestMapping(value = "/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	@GetMapping
	public ResponseEntity<List<CategoriaDTO>> listarCategoria() {

		List<CategoriaDTO> lista = this.categoriaService.listarCategoria();

		return ResponseEntity.ok(lista);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id) {

		CategoriaDTO dto = this.categoriaService.buscarPorId(id);

		return ResponseEntity.ok().body(dto);
	}

	@GetMapping(value = "/{id}/videos")
	public ResponseEntity<CategoriaDTO> obterCategoriaVideos(@PathVariable Long id) {

		CategoriaDTO dto = this.categoriaService.obterCategoriaVideos(id);

		return ResponseEntity.ok().body(dto);
	}

	@PostMapping
	public ResponseEntity<CategoriaDTO> criarCategoria(@Valid @RequestBody CategoriaDTO dto) {

		dto = this.categoriaService.criarCategoria(dto);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				                             .path("/{id}")
				                             .buildAndExpand(dto.getId())
				                             .toUri();

		return ResponseEntity.created(uri).body(dto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoriaDTO> atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {

		dto = this.categoriaService.atualizarCategoria(id, dto);

		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deletarCategoria(@PathVariable Long id) {

		this.categoriaService.deletarCategoria(id);

		return ResponseEntity.ok(new String("Categoria deletada com sucesso."));
	}
}