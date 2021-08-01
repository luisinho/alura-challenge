package br.com.alura.alurafix.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.alurafix.dto.CategoriaDTO;
import br.com.alura.alurafix.entities.Categoria;
import br.com.alura.alurafix.exceptions.RegisterNotFoundException;
import br.com.alura.alurafix.repositories.CategoriaRepository;
import br.com.alura.alurafix.services.exceptions.DataBaseException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Transactional(readOnly = true)
	public List<CategoriaDTO> listarCategoria() {

		List<Categoria> lista = this.categoriaRepository.findAll();

		return lista.stream().map(categoria -> new CategoriaDTO(categoria)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoriaDTO buscarPorId(Long id) {

		Optional<Categoria> obj = this.categoriaRepository.findById(id);

		Categoria entity = obj.orElseThrow(() -> new RegisterNotFoundException());

		return new CategoriaDTO(entity);
	}

	@Transactional
	public 	CategoriaDTO criarCategoria(CategoriaDTO  dto) {

		Categoria entity = new Categoria();

		try {

			this.copyDtoToEntity(dto, entity);

			entity = this.categoriaRepository.save(entity);

		}catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao criar a categoria!");
		}

		return new CategoriaDTO(entity);
	}

	@Transactional
	public CategoriaDTO atualizarCategoria(Long id, CategoriaDTO dto) {

		Categoria entity = null;

		try {

			entity = this.categoriaRepository.getById(id);

			this.copyDtoToEntity(dto, entity);

			entity = this.categoriaRepository.save(entity);

		} catch (EmptyResultDataAccessException e) {
			throw new DataBaseException("Id não encontrado " + id);
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao atualizar a categoria!" + id);
		}

		return new CategoriaDTO(entity);
	}

	private void copyDtoToEntity(CategoriaDTO dto, Categoria entity) {

		entity.setCor(dto.getCor().trim());
		entity.setTitulo(dto.getTitulo().trim());
	}
}
