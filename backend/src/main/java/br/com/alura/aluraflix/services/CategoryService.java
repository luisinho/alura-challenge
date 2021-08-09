package br.com.alura.aluraflix.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAllPaged() {

		List<Category> lista = this.categoriaRepository.findAll();

		return lista.stream().map(categoria -> new CategoryDTO(categoria)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {

		Optional<Category> obj = this.categoriaRepository.findById(id);

		Category entity = obj.orElseThrow(() -> new RegisterNotFoundException("Categoria não encontrada!"));

		return new CategoryDTO(entity);
	}

	@Transactional(readOnly = true)
	public Category findById(CategoryDTO dto) {

		Optional<Category> obj = this.categoriaRepository.findById(dto.getId());

		Category entity = obj.orElseThrow(() -> new RegisterNotFoundException("Categoria não encontrada!"));

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

		Category entity = obj.orElseThrow(() -> new RegisterNotFoundException("Categoria não encontrada!"));

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
			throw new DataBaseException("Ocorreu um erro ao criar a categoria!");
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
			throw new DataBaseException("Ocorreu um erro ao atualizar, ID da categoria não encontrado!");
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao atualizar a categoria com o ID " + id);
		}

		return new CategoryDTO(entity);
	}

	@Transactional
	public void delete(Long id) {

		try {

			this.validateVideoByCategory(id);

			this.categoriaRepository.deleteById(id);

		} catch (EmptyResultDataAccessException e) {
			throw new DataBaseException("Ocorreu um erro ao deletar, ID da categoria não encontrado!");
		}catch  (RegraNegocioException e) {
			throw new DataBaseException(e.getMessage());
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao deletar a categoria com o ID " + id);
		}
	}

	@Transactional(readOnly = true)
	private void validateVideoByCategory(Long id) {

		long count = this.categoriaRepository.countVideoPorCategoria(id);

		if (count > 0) {
			throw new RegraNegocioException("A categoria não pode ser removida, está relacionada com um ou mais vídeos!");
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
			throw new RegraNegocioException("Não existe categoria cadastrada no banco de dados.A primeira categoria deve ser cadastrada com o título conforme o exemplo LIVRE.");
		}

		count = this.categoriaRepository.countByTituloIgnoreCase(dto.getTitulo());

		if (count > 0) {
			throw new RegraNegocioException("Já existe a categoria com o título " + dto.getTitulo());
		}
	}
}