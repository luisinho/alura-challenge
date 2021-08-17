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

import br.com.alura.aluraflix.dto.CategoryDTO;
import br.com.alura.aluraflix.entities.Category;
import br.com.alura.aluraflix.exceptions.RegisterNotFoundException;
import br.com.alura.aluraflix.exceptions.RegraNegocioException;
import br.com.alura.aluraflix.repositories.CategoryRepository;
import br.com.alura.aluraflix.services.exceptions.DataBaseException;

@Service
public class CategoryService {

	private static Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

	@Autowired
	private CategoryRepository categoriaRepository;

	@Autowired
	private MessageSource messageSource;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {

		LOGGER.info("START METHOD CategoryService.findAllPage: {} " + pageRequest.toString());

		Page<Category> page;

		try {

			page = this.categoriaRepository.findAll(pageRequest);

			if(page.isEmpty() || page.getContent().isEmpty()) {

				throw new RegisterNotFoundException(this.messageSource.getMessage("category.not.found.data", null, null));
			}

		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo CategoryService.findAllPaged " + e);
			throw new DataBaseException(this.messageSource.getMessage("category.error.listing", null, null));
		}

		LOGGER.info("END METHOD CategoryService.findAllPage");

		return page.map(categoria -> new CategoryDTO(categoria));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {

		LOGGER.info("START METHOD CategoryService.findById: {} " + id);

		Category entity = new Category();

		try {

			Optional<Category> obj = this.categoriaRepository.findById(id);

			entity = obj.orElseThrow(() -> new RegisterNotFoundException(this.messageSource.getMessage("category.not.found", null, null)));

		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo CategoryService.findById " + e);
			throw new DataBaseException(this.messageSource.getMessage("category.error.search", null, null) + " " + id);
		}

		LOGGER.info("END METHOD CategoryService.findById: {} " + id);

		return new CategoryDTO(entity);
	}

	@Transactional(readOnly = true)
	public Category findById(CategoryDTO dto) {

		LOGGER.info("START METHOD CategoryService.findById: {} " + dto.toString());

		Category entity = new Category();
		
		try {
			
			Optional<Category> obj = this.categoriaRepository.findById(dto.getId());
			
			entity = obj.orElseThrow(() -> new RegisterNotFoundException(this.messageSource.getMessage("category.not.found", null, null)));

		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo CategoryService.findById " + e);
			throw new DataBaseException(this.messageSource.getMessage("category.error.search", null, null) + " " + dto.getId());
		}

		LOGGER.info("END METHOD CategoryService.findById: {} " + dto.toString());

		return entity;
	}

	@Transactional(readOnly = true)
	public CategoryDTO getVideoByCategory(Long id) {

		LOGGER.info("START METHOD CategoryService.getVideoByCategory: {} " + id);

		Category entity = new Category();

		try {
			entity = this.categoriaRepository.getVideoByCategory(id);
		}catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo CategoryService.getVideoByCategory " + e);
			throw new DataBaseException(this.messageSource.getMessage("category.video.by.id", null, null) + " " + id);
		}

		LOGGER.info("END METHOD CategoryService.getVideoByCategory: {} " + id);

		return new CategoryDTO(entity, entity.getVideos());
	}

	@Transactional(readOnly = true)
	public Category getCategoryByTitle(String titulo) {

		LOGGER.info("START METHOD CategoryService.getCategoryByTitle: {} " + titulo);

		Category entity = new Category();

		try {

			Optional<Category> obj = this.categoriaRepository.findByTituloIgnoreCase(titulo);

			entity = obj.orElseThrow(() -> new RegisterNotFoundException(this.messageSource.getMessage("category.not.found", null, null)));

		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo CategoryService.getVideoByCategory " + e);
		}

		LOGGER.info("END METHOD CategoryService.getCategoryByTitle");

		return entity;
	}

	@Transactional
	public 	CategoryDTO save(CategoryDTO  dto) {

		LOGGER.info("START METHOD CategoryService.save: {} " + dto.toString());

		Category entity = new Category();

		try {

			this.validateCategoryName(dto);

			this.copyDtoToEntity(dto, entity);

			entity = this.categoriaRepository.save(entity);

		} catch(RegraNegocioException e) {
			throw new RegraNegocioException(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo CategoryService.save " + e);
			throw new DataBaseException(this.messageSource.getMessage("category.error.creating", null, null));
		}

		LOGGER.info("END METHOD CategoryService.save");

		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {

		LOGGER.info("START METHOD CategoryService.update: {}, {} " + id + "-" + dto.toString());

		Category entity = null;

		try {

			entity = this.categoriaRepository.getById(id);

			this.copyDtoToEntity(dto, entity);

			entity = this.categoriaRepository.save(entity);

		} catch (EmptyResultDataAccessException e) {
			throw new DataBaseException(this.messageSource.getMessage("category.error.creating", null, null));
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException(this.messageSource.getMessage("integrity.violation", null, null));
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo CategoryService.update " + e);
			throw new DataBaseException(this.messageSource.getMessage("category.error.updating.with.the.id", null, null) + " " + id);
		}

		LOGGER.info("START METHOD CategoryService.update");

		return new CategoryDTO(entity);
	}

	@Transactional
	public void delete(Long id) {

		LOGGER.info("START METHOD CategoryService.delete: {} " + id);

		try {

			this.validateVideoByCategory(id);

			this.categoriaRepository.deleteById(id);
			
			LOGGER.info("END METHOD CategoryService.delete");

		} catch (EmptyResultDataAccessException e) {
			throw new DataBaseException(this.messageSource.getMessage("category.error.deleting.id.not.found", null, null));
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException(this.messageSource.getMessage("integrity.violation", null, null));
		}catch  (RegraNegocioException e) {
			throw new DataBaseException(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo CategoryService.delete " + e);
			throw new DataBaseException(this.messageSource.getMessage("category.error.deleting.with.the.id", null, null) + " " + id);
		}
	}

	@Transactional(readOnly = true)
	private void validateVideoByCategory(Long id) throws Exception {

		LOGGER.info("START METHOD CategoryService.validateVideoByCategory: {} " + id);

		long count = this.categoriaRepository.countVideoPorCategoria(id);

		if (count > 0) {
			throw new RegraNegocioException(this.messageSource.getMessage("category.cannot.removed", null, null));
		}

		LOGGER.info("END METHOD CategoryService.validateVideoByCategory");
	}

	private void copyDtoToEntity(CategoryDTO dto, Category entity) throws Exception {

		entity.setCor(dto.getCor().trim());
		entity.setTitulo(dto.getTitulo().trim());
	}

	private void validateCategoryName(CategoryDTO dto) throws Exception {

		LOGGER.info("START METHOD CategoryService.validateCategoryName: {} " + dto.toString());

		long count = this.categoriaRepository.count();

		if (dto.getTitulo().equalsIgnoreCase("LIVRE")) {
			dto.setTitulo(dto.getTitulo().toUpperCase());
		}

		if (count == 0 && !dto.getTitulo().equals("LIVRE")) {
			throw new RegraNegocioException(this.messageSource.getMessage("category.title.free.not.exist", null, null));
		}

		count = this.categoriaRepository.countByTituloIgnoreCase(dto.getTitulo());

		if (count > 0) {
			throw new RegraNegocioException(this.messageSource.getMessage("category.title.exist", null, null) + " " + dto.getTitulo());
		}
	}
}