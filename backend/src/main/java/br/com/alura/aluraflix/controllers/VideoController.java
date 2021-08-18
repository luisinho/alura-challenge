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

import br.com.alura.aluraflix.dto.VideoDTO;
import br.com.alura.aluraflix.services.VideoService;

@RestController
@RequestMapping(value = "/videos")
public class VideoController {

	private static Logger LOGGER = LoggerFactory.getLogger(VideoController.class);

	@Autowired
	private VideoService videoService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping
	public ResponseEntity<Page<VideoDTO>> findAllPaged(
			@RequestParam(value = "search", defaultValue = "") String search,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "5") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
			@RequestParam(value = "orderBy", defaultValue = "titulo") String orderBy) {

		StringBuffer params = new StringBuffer();
		params.append(search).append("\n");
		params.append(page).append("\n");
		params.append(linesPerPage).append("\n");
		params.append(direction).append("\n");
		params.append(orderBy).append("\n");

		LOGGER.info("START METHOD VideoController.findAllPaged: {} {} {} {} {} " + params);

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction),  orderBy);

		Page<VideoDTO> list = this.videoService.findAllPaged(search, pageRequest);

		LOGGER.info("END METHOD VideoController.findAllPaged");

		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/free")
	public ResponseEntity<Page<VideoDTO>> findFreeVideo(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "5") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
			@RequestParam(value = "orderBy", defaultValue = "titulo") String orderBy) {

		StringBuffer params = new StringBuffer();
		params.append(page).append("\n");
		params.append(linesPerPage).append("\n");
		params.append(direction).append("\n");
		params.append(orderBy).append("\n");

		LOGGER.info("START METHOD VideoController.findFreeVideo: {} {} {} {} " + params);

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction),  orderBy);

		Page<VideoDTO> list = this.videoService.findFreeVideo(pageRequest);

		LOGGER.info("END METHOD VideoController.findAllPaged");

		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<VideoDTO> findById(@PathVariable Long id) {

		LOGGER.info("START METHOD VideoController.findById: {} " + id);

		VideoDTO dto = this.videoService.findById(id);

		LOGGER.info("END METHOD VideoController.findById");

		return ResponseEntity.ok().body(dto);
	}

	@PostMapping
	public ResponseEntity<VideoDTO> save(@Valid @RequestBody VideoDTO dto) {

		LOGGER.info("START METHOD VideoController.save: {} " + dto.toString());

		dto = this.videoService.save(dto);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				                             .path("/{id}")
				                             .buildAndExpand(dto.getId())
				                             .toUri();

		LOGGER.info("END METHOD VideoController.save: {} " + dto.toString());

		return ResponseEntity.created(uri).body(dto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<VideoDTO> update(@PathVariable Long id, @Valid @RequestBody VideoDTO dto) {

		LOGGER.info("START METHOD VideoController.update: {} " +  id + " - " + dto.toString());

		dto = this.videoService.update(id, dto);

		LOGGER.info("END METHOD VideoController.update: {} " + dto.toString());

		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {

		LOGGER.info("START METHOD VideoController.delete: {} " +  id);

		this.videoService.delete(id);

		LOGGER.info("END METHOD VideoController.delete");

		return ResponseEntity.ok().body(new String(this.messageSource.getMessage("video.deleting.success", null, null)));
	}
}