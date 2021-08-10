package br.com.alura.aluraflix.services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
	public Page<VideoDTO> findAllPaged(String search, PageRequest pageRequest) {

		Page<Video> page;

		try {

			if (!"".equals(search)) {

				page = this.videoRepository.findTitle(search, pageRequest);
				this.validateDoesNotExistPage(search, page);

			} else {
				page = this.videoRepository.findAll(pageRequest);
				this.validateDoesNotExistPage(search, page);
			}

		} catch (RegisterNotFoundException e) {
			throw new RegisterNotFoundException(e.getMessage());
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao listar os videos.");
		}

		return page.map(video -> new VideoDTO(video));
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

	private void validateDoesNotExistPage(String search, Page<Video> page) {

        if (!"".equals(search) && page.getContent().isEmpty()) {
        	throw new RegisterNotFoundException("Não foi encontrado o vídeo com o titulo " + search);
		} else if("".equals(search) && page.getContent().isEmpty()) {
			throw new RegisterNotFoundException("Não foi encontrado dados de vídeos.");
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