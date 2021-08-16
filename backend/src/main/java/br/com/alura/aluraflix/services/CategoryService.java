package br.com.alura.aluraflix.services;

import java.util.Optional;
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

	@Autowired
	private CategoryRepository categoriaRepository;

	@Autowired
	private MessageSource messageSource;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {

		Page<Category> page = this.categoriaRepository.findAll(pageRequest);

		return page.map(categoria -> new CategoryDTO(categoria));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {

		Optional<Category> obj = this.categoriaRepository.findById(id);

		Category entity = obj.orElseThrow(() -> new RegisterNotFoundException(this.messageSource.getMessage("category.not.found", null, null)));

		return new CategoryDTO(entity);
	}

	@Transactional(readOnly = true)
	public Category findById(CategoryDTO dto) {

		Optional<Category> obj = this.categoriaRepository.findById(dto.getId());

		Category entity = obj.orElseThrow(() -> new RegisterNotFoundException(this.messageSource.getMessage("category.not.found", null, null)));

		return entity;
	}

	@Transactional(readOnly = true)
	public CategoryDTO getVideoByCategory(Long id) {

		Category entity = this.categoriaRepository.getVideoByCategory(id);

		return new CategoryDTO(entity, entity.getVideos());
	}

	@Transactional(readOnly = true)
	public Category getCategoryByTitle(String titulo) {

		Optional<Category> obj = this.categoriaRepository.findByTituloIgnoreCase(titulo);

		Category entity = obj.orElseThrow(() -> new RegisterNotFoundException(this.messageSource.getMessage("category.not.found", null, null)));

		return entity;
	}

	@Transactional
	public 	CategoryDTO save(CategoryDTO  dto) {

		Category entity = new Category();

		try {

			this.validateCategoryName(dto);

			this.copyDtoToEntity(dto, entity);

			entity = this.categoriaRepository.save(entity);

		} catch(RegraNegocioException e) {
			throw new RegraNegocioException(e.getMessage());
		} catch (Exception e) {
			throw new DataBaseException(this.messageSource.getMessage("category.error.creating", null, null));
		}

		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {

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
			throw new DataBaseException(this.messageSource.getMessage("category.error.updating.with.the.id", null, null) + " " + id);
		}

		return new CategoryDTO(entity);
	}

	@Transactional
	public void delete(Long id) {

		try {

			this.validateVideoByCategory(id);

			this.categoriaRepository.deleteById(id);

		} catch (EmptyResultDataAccessException e) {
			throw new DataBaseException(this.messageSource.getMessage("category.error.deleting.id.not.found", null, null));
		}catch  (RegraNegocioException e) {
			throw new DataBaseException(e.getMessage());
		} catch (Exception e) {
			throw new DataBaseException(this.messageSource.getMessage("category.error.deleting.with.the.id", null, null) + " " + id);
		}
	}

	@Transactional(readOnly = true)
	private void validateVideoByCategory(Long id) {

		long count = this.categoriaRepository.countVideoPorCategoria(id);

		if (count > 0) {
			throw new RegraNegocioException(this.messageSource.getMessage("category.cannot.removed", null, null));
		}
	}

	private void copyDtoToEntity(CategoryDTO dto, Category entity) {

		entity.setCor(dto.getCor().trim());
		entity.setTitulo(dto.getTitulo().trim());
	}

	private void validateCategoryName(CategoryDTO dto) {

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