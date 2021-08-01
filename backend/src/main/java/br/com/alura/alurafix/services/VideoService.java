package br.com.alura.alurafix.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.alurafix.dto.VideoDTO;
import br.com.alura.alurafix.entities.Video;
import br.com.alura.alurafix.exceptions.RegisterNotFoundException;
import br.com.alura.alurafix.repositories.VideoRepository;
import br.com.alura.alurafix.services.exceptions.DataBaseException;

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

		try {

			this.copyDtoToEntity(dto, entity);

			entity = this.videoRepository.save(entity);

		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao criar o video!");
		}

		return new VideoDTO(entity);
	}

	@Transactional
	public VideoDTO atualizarVideo(Long id, VideoDTO dto) {

		Video entity = null;

		try {

			entity = this.videoRepository.getById(id);
			
			this.copyDtoToEntity(dto, entity);

			entity = this.videoRepository.save(entity);

		} catch (EmptyResultDataAccessException e) {
			throw new DataBaseException("Id não encontrado " + id);
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao atualizar o video!" + id);
		}

		return new VideoDTO(entity);
	}

	@Transactional
	public void deletarVideo(Long id) {

		try {

			this.videoRepository.deleteById(id);

		} catch (EmptyResultDataAccessException e) {
			throw new DataBaseException("Id não encontrado " + id);
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao deletar o video" + id);
		}
	}

	private void copyDtoToEntity(VideoDTO dto, Video entity) {

		entity.setDescricao(dto.getDescricao().trim());
		entity.setTitulo(dto.getTitulo().trim());
		entity.setUrl(dto.getUrl().trim());
	}
}