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
import br.com.alura.aluraflix.exceptions.RegraNegocioException;
import br.com.alura.aluraflix.repositories.UserRepository;
import br.com.alura.aluraflix.services.exceptions.DataBaseException;

@Service
public class UserService implements UserDetailsService {

	private static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private MessageSource messageSource;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(PageRequest pageRequest) {
		
		LOGGER.info("START METHOD UserService.findAllPage: {} " + pageRequest.toString());

		try {

			Page<User> list = this.userRepository.findAll(pageRequest);
			
			LOGGER.info("END METHOD UserService.findAllPage");

			return list.map(x -> new UserDTO(x));

		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo UserService.findAllPage " + e);
			throw new DataBaseException(this.messageSource.getMessage("user.error.listing", null, null));
		}
	}

	@Transactional
	public UserDTO findById(Long id) {

		LOGGER.info("START METHOD UserService.findById: {} " + id);

		User user = new User();

		try {

			Optional<User> obj = this.userRepository.findById(id);

			user = obj.orElseThrow(() -> new RegisterNotFoundException(this.messageSource.getMessage("user.not.found.with.the.id", null, null)));

		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo UserService.findById " + e);
			throw new DataBaseException(this.messageSource.getMessage("user.error.search", null, null) + " " + id);
		}

		LOGGER.info("END METHOD UserService.findById ");

		return new UserDTO(user);
	}

	@Transactional
	public UserDTO save(UserInsertDTO dto) {
		
		LOGGER.info("START METHOD UserService.save ");

		User entity = new User();

		try {
			
		  this.validateUserEmail(dto);

		  this.copyDtoToEntity(dto, entity);

		  entity.setPassword(this.passwordEncoder.encode(dto.getPassword()));

		  entity = this.userRepository.save(entity);

		} catch(RegraNegocioException e) {
			throw new RegraNegocioException(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo UserService.save " + e);
			throw new DataBaseException(this.messageSource.getMessage("user.error.creating", null, null));
		}
		
		LOGGER.info("END METHOD UserService.save ");

		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {

		LOGGER.info("START METHOD UserService.update ");

		User entity = new User();

		try {

			this.validateUserEmail(dto);

			entity = this.userRepository.getById(id);

			this.copyDtoToEntity(dto, entity);

			entity = this.userRepository.save(entity);

		} catch (EmptyResultDataAccessException e) {			
			throw new RegisterNotFoundException(this.messageSource.getMessage("user.error.updating.id.not.found", null, null));
		}catch  (DataIntegrityViolationException e) {
			LOGGER.error("Ocorreu um erro no metodo UserService.update() " + e);
			throw new DataBaseException(this.messageSource.getMessage("integrity.violation", null, null));
		} catch(RegraNegocioException e) {
			throw new RegraNegocioException(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro no metodo UserService.update() " + e);
			throw new DataBaseException(this.messageSource.getMessage("user.error.updating.with.the.id", null, null) +  " "  + id);
		}

		LOGGER.info("END METHOD UserService.save ");

		return new UserDTO(entity);
	}

	private void copyDtoToEntity(UserDTO dto, User entity) throws Exception {
		entity.setEmail(dto.getEmail());
		entity.setNome(dto.getNome());
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		LOGGER.info("START METHOD UserService.loadUserByUsername: {}" + username);

		User user = this.userRepository.findByEmail(username);

		if (user == null) {
			LOGGER.error(this.messageSource.getMessage("use.not.found", null, null) + ": " + username);
			throw new UsernameNotFoundException(this.messageSource.getMessage("use.not.found", null, null));
		}

		LOGGER.info("END METHOD UserService.loadUserByUsername: {}");

		return user;
	}

	private void validateUserEmail(UserDTO dto) {

		long count = this.userRepository.countByEmailIgnoreCase(dto.getEmail());

		if (count > 0) {
			throw new RegraNegocioException(this.messageSource.getMessage("user.email.exist", null, null) + " " + dto.getEmail());
		}
	}
}