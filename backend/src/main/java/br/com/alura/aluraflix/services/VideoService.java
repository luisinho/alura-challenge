package br.com.alura.aluraflix.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.aluraflix.dto.VideoDTO;
import br.com.alura.aluraflix.entities.Category;
import br.com.alura.aluraflix.entities.Video;
import br.com.alura.aluraflix.exceptions.RegisterNotFoundException;
import br.com.alura.aluraflix.repositories.VideoRepository;
import br.com.alura.aluraflix.services.exceptions.DataBaseException;

@Service
public class VideoService {

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private CategoryService categoriaService;

	@Transactional(readOnly = true)
	public List<VideoDTO> findAllPaged(String search) {

		if (!"".equals(search)) {

			Optional<Video> obj = this.videoRepository.findByTitulo(search);

			Video entity = obj.orElseThrow(() -> new RegisterNotFoundException("Não foi encontrado o vídeo " + search));

			return Arrays.asList(new VideoDTO(entity));
		}

		List<Video> lista = this.videoRepository.findAll();

		return lista.stream().map(video -> new VideoDTO(video)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public VideoDTO findById(Long id) {

		Optional<Video> obj = this.videoRepository.findById(id);

		Video entity = obj.orElseThrow(() -> new RegisterNotFoundException("Não foi encontrado o vídeo com o ID " + id));

		return new VideoDTO(entity);
	}

	@Transactional
	public VideoDTO save(VideoDTO dto) throws Exception{

		Video entity = new Video();

		try {

			this.copyDtoToEntity(dto, entity);

			entity = this.videoRepository.save(entity);

		} catch(RegisterNotFoundException e) {
			throw new RegisterNotFoundException(e.getMessage());
		} catch (Exception e) {
			throw new DataBaseException(e.getMessage());
		}

		return new VideoDTO(entity);
	}

	@Transactional
	public VideoDTO update(Long id, VideoDTO dto) {

		Video entity = null;

		try {

			entity = this.videoRepository.getById(id);
			
			this.copyDtoToEntity(dto, entity);

			entity = this.videoRepository.save(entity);

		} catch (EmptyResultDataAccessException e) {
			throw new RegisterNotFoundException("Id não encontrado " + id);
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao atualizar o video " + id);
		}

		return new VideoDTO(entity);
	}

	@Transactional
	public void delete(Long id) {

		try {

			this.videoRepository.deleteById(id);

		} catch (EmptyResultDataAccessException e) {
			throw new RegisterNotFoundException("Id não encontrado " + id);
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao deletar o video" + id);
		}
	}

	private void copyDtoToEntity(VideoDTO dto, Video entity) {
		
		final String CATEGORIA_TITULO_LIVRE = "LIVRE";

		entity.setDescricao(dto.getDescricao().trim());
		entity.setTitulo(dto.getTitulo().trim());
		entity.setUrl(dto.getUrl().trim());

		if (dto.getCategoria() == null || dto.getCategoria().getId() == null) {

			Category  categoria = this.categoriaService.getCategoryByTitle(CATEGORIA_TITULO_LIVRE);
			entity.setCategoria(categoria);

		} else {

			Category  categoria = this.categoriaService.findById(dto.getCategoria());
			entity.setCategoria(categoria);
		}
	}
}