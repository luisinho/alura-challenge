package br.com.alura.aluraflix.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.aluraflix.dto.UserDTO;
import br.com.alura.aluraflix.dto.UserInsertDTO;
import br.com.alura.aluraflix.dto.UserUpdateDTO;
import br.com.alura.aluraflix.entities.User;
import br.com.alura.aluraflix.exceptions.RegisterNotFoundException;
import br.com.alura.aluraflix.repositories.UserRepository;
import br.com.alura.aluraflix.services.exceptions.DataBaseException;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Transactional
	public UserDTO save(UserInsertDTO dto) {

		User entity = new User();

		try {

		  this.copyDtoToEntity(dto, entity);

		  entity.setPassword(this.passwordEncoder.encode(dto.getPassword()));

		  entity = this.userRepository.save(entity);

		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao criar o usuario ");
		}

		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {

		try {

			User entity = this.userRepository.getById(id);

			this.copyDtoToEntity(dto, entity);

			entity = this.userRepository.save(entity);

			return new UserDTO(entity);

		} catch (EmptyResultDataAccessException e) {
			throw new RegisterNotFoundException("Id não encontrado " + id);
		}catch  (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao atualizar o usuario " + id);
		}
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setEmail(dto.getEmail());
		entity.setNome(dto.getNome());
	}
}