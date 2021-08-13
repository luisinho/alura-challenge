package br.com.alura.aluraflix.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class UserService implements UserDetailsService {

	private static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPage(PageRequest pageRequest) {

		try {

			Page<User> list = this.userRepository.findAll(pageRequest);

			return list.map(x -> new UserDTO(x));

		} catch (Exception e) {
			throw new DataBaseException("Ocorreu um erro ao listar os usuarios.");
		}
	}

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

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		LOGGER.info("INICIO NO METODO loadUserByUsername: {}" + username);

		User user = this.userRepository.findByEmail(username);

		if (user == null) {
			LOGGER.error("Usuário não encontrado: " + username);
			throw new UsernameNotFoundException("Usuário não encontrado!");
		}

		LOGGER.info("FIM DO METODO loadUserByUsername: {}");

		return user;
	}	
}