package br.com.alura.alurafix.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.alurafix.dto.VideoDTO;
import br.com.alura.alurafix.entities.Video;
import br.com.alura.alurafix.exceptions.RegisterNotFoundException;
import br.com.alura.alurafix.repositories.VideoRepository;

@Service
public class VideoService {

	@Autowired
	private VideoRepository videoRepository;

	@Transactional(readOnly = true)
	public List<VideoDTO> listarVideo() {

		List<Video> lista = this.videoRepository.findAll();

		return lista.stream().map(video -> new VideoDTO(video)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public VideoDTO buscarPorId(Long id) {

		Optional<Video> obj = this.videoRepository.findById(id);

		Video entity = obj.orElseThrow(() -> new RegisterNotFoundException());

		return new VideoDTO(entity);
	}

	@Transactional
	public VideoDTO criarVideo(VideoDTO dto) {

		Video entity = new Video();
		this.copyDtoToEntity(dto, entity);
		
		entity = this.videoRepository.save(entity);

		return new VideoDTO(entity);
	}

	private void copyDtoToEntity(VideoDTO dto, Video entity) {

		entity.setDescricao(dto.getDescricao());
		entity.setTitulo(dto.getTitulo());
		entity.setUrl(dto.getUrl());
	}
}