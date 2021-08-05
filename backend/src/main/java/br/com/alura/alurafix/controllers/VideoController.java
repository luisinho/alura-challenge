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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.alura.alurafix.dto.VideoDTO;
import br.com.alura.alurafix.services.VideoService;

@RestController
@RequestMapping(value = "/videos")
public class VideoController {

	@Autowired
	private VideoService videoService;

	@GetMapping
	public ResponseEntity<List<VideoDTO>> findAllPaged(
			@RequestParam(value = "search", defaultValue = "") String search) {

		List<VideoDTO> lista = this.videoService.findAllPaged(search);

		return ResponseEntity.ok().body(lista);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<VideoDTO> findById(@PathVariable Long id) {

		VideoDTO dto = this.videoService.findById(id);

		return ResponseEntity.ok().body(dto);
	}

	@PostMapping
	public ResponseEntity<VideoDTO> save(@Valid @RequestBody VideoDTO dto) throws Exception {

		dto = this.videoService.save(dto);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				                             .path("/{id}")
				                             .buildAndExpand(dto.getId())
				                             .toUri();

		return ResponseEntity.created(uri).body(dto);		
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<VideoDTO> update(@PathVariable Long id, @Valid @RequestBody VideoDTO dto) {

		dto = this.videoService.update(id, dto);

		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {

		this.videoService.delete(id);

		return ResponseEntity.ok().body(new String("Vídeo removido com sucesso."));
	}
}