package br.com.alura.aluraflix.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import br.com.alura.aluraflix.exceptions.RegraNegocioException;
import br.com.alura.aluraflix.repositories.VideoRepository;
import br.com.alura.aluraflix.services.exceptions.DataBaseException;

@Service
public class VideoService {

	private static Logger LOGGER = LoggerFactory.getLogger(VideoService.class);

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private CategoryService categoriaService;

	@Autowired
	private MessageSource messageSource;

	@Transactional(readOnly = true)
	public Page<VideoDTO> findAllPaged(String search, PageRequest pageRequest) {

		LOGGER.info("START METHOD VideoService.findAllPaged: {} " + pageRequest.toString());

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
			LOGGER.error("Ocorreu um erro no metodo VideoService.findAllPaged " + e);
			throw new DataBaseException(this.messageSource.getMessage("video.error.listing", null, null));
		}

		LOGGER.info("END METHOD VideoService.findAllPaged");

		return page.map(video -> new VideoDTO(video));
	}

	@Transactional(readOnly = true)
	public Page<VideoDTO> findFreeVideo(PageRequest pageRequest) {

		LOGGER.info("START METHOD VideoService.findFreeVideo: {} " + pageRequest.toString());

		Page<Video> page;

		try {

			page = this.videoRepository.findFreeVideo(pageRequest);

			this.validateDoesNotExistPage(null, page);

		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo VideoService.findFreeVideo " + e);
			throw new DataBaseException(this.messageSource.getMessage("video.error.listing", null, null));
		}

		LOGGER.info("END METHOD VideoService.findFreeVideo");

		return page.map(video -> new VideoDTO(video));
	}

	@Transactional(readOnly = true)
	public VideoDTO findById(Long id) {

		LOGGER.info("START METHOD VideoService.findById: {} " + id);

		Video entity = new Video();

		try {

			Optional<Video> obj = this.videoRepository.findById(id);

			entity = obj.orElseThrow(() -> new RegisterNotFoundException(this.messageSource.getMessage("video.not.found.with.the.id", null, null) + " " + id));

		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo VideoService.findById " + e);
			throw new DataBaseException(this.messageSource.getMessage("video.error.search", null, null));
		}

		LOGGER.info("END METHOD VideoService.findById");

		return new VideoDTO(entity);
	}

	@Transactional
	public VideoDTO save(VideoDTO dto) {

		LOGGER.info("START METHOD VideoService.save: {} " + dto.toString());

		Video entity = new Video();

		try {

			this.validateVideoTitle(dto);

			this.copyDtoToEntity(dto, entity);

			entity = this.videoRepository.save(entity);

		} catch(RegisterNotFoundException e) {
			throw new RegisterNotFoundException(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo VideoService.save " + e);
			throw new DataBaseException(e.getMessage());
		}

		LOGGER.info("END METHOD VideoService.save");

		return new VideoDTO(entity);
	}

	@Transactional
	public VideoDTO update(Long id, VideoDTO dto) {

		LOGGER.info("START METHOD VideoService.update: {} {} " + id + " - " + dto.toString());

		Video entity = null;

		try {

			this.validateVideoTitle(dto);

			entity = this.videoRepository.getById(id);
			
			this.copyDtoToEntity(dto, entity);

			entity = this.videoRepository.save(entity);

		} catch (EmptyResultDataAccessException e) {
			throw new RegisterNotFoundException(this.messageSource.getMessage("video.error.updating.id.not.found", null, null));
		}catch  (DataIntegrityViolationException e) {
			LOGGER.error("Ocorreu um erro no metodo VideoService.update " + e);
			throw new DataBaseException(this.messageSource.getMessage("integrity.violation", null, null));
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo VideoService.update " + e);
			throw new DataBaseException(this.messageSource.getMessage("video.error.updating.with.the.id", null, null) + " " + id);
		}

		LOGGER.info("END METHOD VideoService.update");

		return new VideoDTO(entity);
	}

	@Transactional
	public void delete(Long id) {

		LOGGER.info("START METHOD VideoService.delete: {} " + id);

		try {

			this.videoRepository.deleteById(id);

			LOGGER.info("END METHOD VideoService.delete");

		} catch (EmptyResultDataAccessException e) {
			throw new RegisterNotFoundException(this.messageSource.getMessage("video.error.deleting.id.not.found", null, null));
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException(this.messageSource.getMessage("integrity.violation", null, null));
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo VideoService.delete " + e);
			throw new DataBaseException(this.messageSource.getMessage("video.error.deleting.with.the.id", null, null) + " " + id);
		}
	}

	private void validateDoesNotExistPage(String search, Page<Video> page) throws Exception {

        if (!"".equals(search) && page.getContent().isEmpty()) {
        	throw new RegisterNotFoundException(this.messageSource.getMessage("video.not.found.search", null, null) + " " + search);
		} else if("".equals(search) && page.getContent().isEmpty()) {
			throw new RegisterNotFoundException(this.messageSource.getMessage("video.not.found.data", null, null));
		}
    }

	private void copyDtoToEntity(VideoDTO dto, Video entity) throws Exception {
		
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

	private void validateVideoTitle(VideoDTO dto) throws Exception {

		LOGGER.info("START METHOD VideoService.validateVideoTitle: {} " + dto.toString());

		long count = this.videoRepository.countByTituloIgnoreCase(dto.getTitulo());

		if (count > 0) {
			throw new RegraNegocioException(this.messageSource.getMessage("video.title.exist", null, null) + " " + dto.getTitulo());
		}

		LOGGER.info("END METHOD VideoService.validateVideoTitle");
	}
}