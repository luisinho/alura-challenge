package br.com.alura.aluraflix.controllers;

import java.net.URI;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

import br.com.alura.aluraflix.dto.VideoDTO;
import br.com.alura.aluraflix.services.VideoService;

@RestController
@RequestMapping(value = "/videos")
public class VideoController {

	@Autowired
	private VideoService videoService;

	@GetMapping
	public ResponseEntity<Page<VideoDTO>> findAllPaged(
			@RequestParam(value = "search", defaultValue = "") String search,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "5") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
			@RequestParam(value = "orderBy", defaultValue = "titulo") String orderBy) {

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction),  orderBy);

		Page<VideoDTO> list = this.videoService.findAllPaged(search, pageRequest);

		return ResponseEntity.ok().body(list);
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