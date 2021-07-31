package br.com.alura.alurafix.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.alurafix.dto.CategoriaDTO;
import br.com.alura.alurafix.entities.Categoria;
import br.com.alura.alurafix.repositories.CategoriaRepository;
import br.com.alura.alurafix.services.exceptions.DataBaseException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

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

	private void copyDtoToEntity(CategoriaDTO dto, Categoria entity) {

		entity.setCor(dto.getCor());
		entity.setTitulo(dto.getTitulo());
	}
}
