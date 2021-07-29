package br.com.alura.alurafix.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public ResponseEntity<List<VideoDTO>> listarVideo() {

		List<VideoDTO> lista = this.videoService.listarVideo();

		return ResponseEntity.ok().body(lista);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<VideoDTO> buscarPorId(@PathVariable Long id) {

		VideoDTO dto = this.videoService.buscarPorId(id);

		return ResponseEntity.ok().body(dto);
	}

	@PostMapping
	public ResponseEntity<VideoDTO> criarVideo(@Valid @RequestBody VideoDTO dto) {

		dto = this.videoService.criarVideo(dto);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				                             .path("/{id}")
				                             .buildAndExpand(dto.getId())
				                             .toUri();

		return ResponseEntity.created(uri).body(dto);		
	}
}