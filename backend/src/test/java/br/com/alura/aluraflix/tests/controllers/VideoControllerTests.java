package br.com.alura.aluraflix.tests.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.aluraflix.dto.VideoDTO;
import br.com.alura.aluraflix.exceptions.RegisterNotFoundException;
import br.com.alura.aluraflix.services.VideoService;
import br.com.alura.aluraflix.tests.factory.VideoFactory;

@SpringBootTest
@AutoConfigureMockMvc
public class VideoControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VideoService videoService;

	@Autowired
	private ObjectMapper objectMapper;

	private VideoDTO  newVideoDTO;

	private VideoDTO existingVideoDTO;

	private PageImpl<VideoDTO> page;

	private Long existingId;
	
	private Long nonExistingId;
	
	private String existingTitle;

	private String nonExistingTitle;

	private PageRequest pageRequest;

	@BeforeEach
	void setUp() throws Exception {

		this.existingId = 1l;

		this.nonExistingId = 2l;

		this.existingTitle = "TESTEVIDEO";

		this.nonExistingTitle = "_____";

		this.pageRequest  = PageRequest.of(0, 5, Direction.valueOf("ASC"),  "titulo");

		this.newVideoDTO = VideoFactory.createVideoDTO(null);

		this.existingVideoDTO = VideoFactory.createVideoDTO(this.existingId);

		this.page = new PageImpl<>(Arrays.asList(this.existingVideoDTO));

		when(this.videoService.findAllPaged("", this.pageRequest)).thenReturn(this.page);		

		when(this.videoService.findAllPaged(this.existingTitle, this.pageRequest)).thenReturn(this.page);

		when(this.videoService.findById(this.existingId)).thenReturn(this.existingVideoDTO);

		when(this.videoService.save(any())).thenReturn(this.existingVideoDTO);

		when(this.videoService.update(eq(this.existingId), any())).thenReturn(this.existingVideoDTO);

		when(this.videoService.findAllPaged(this.nonExistingTitle, this.pageRequest)).thenThrow(RegisterNotFoundException.class);

		when(this.videoService.findById(this.nonExistingId)).thenThrow(RegisterNotFoundException.class);

		when(this.videoService.update(eq(this.nonExistingId), any())).thenThrow(RegisterNotFoundException.class);

		doNothing().when(this.videoService).delete(this.existingId);

		doThrow(RegisterNotFoundException.class).when(this.videoService).delete(this.nonExistingId);
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/videos")
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
	}	

	@Test
	public void findByIdShouldReturnVideoDTOWhenIdExists() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/videos/{id}", this.existingId)
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
	}

	@Test
	public void findByIdShouldReturnVideoDTOWhenTitleExists() throws Exception {

		ResultActions result =
				   this.mockMvc.perform(get("/videos?search=" + this.existingTitle)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
	}

	@Test
	public void saveShouldReturnVideoDTOWhenCreated() throws Exception {

		String jsonBody = this.objectMapper.writeValueAsString(this.newVideoDTO);

		ResultActions result =
				   this.mockMvc.perform(post("/videos")
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
	}

	@Test
	public void updateShouldReturnVideoDTOWhenIdExistis() throws Exception {

		String jsonBody = this.objectMapper.writeValueAsString(this.newVideoDTO);

		String expectedTitulo = this.newVideoDTO.getTitulo();

		String expectedDescricao = this.newVideoDTO.getDescricao();

		ResultActions result =
				   this.mockMvc.perform(put("/videos/{id}", this.existingId)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
		result.andExpect(jsonPath("$.titulo").value(expectedTitulo));
		result.andExpect(jsonPath("$.descricao").value(expectedDescricao));
	}

	@Test
	public void deleteShouldReturnNoContentWhenIdExistis() throws Exception {

		ResultActions result =
				   this.mockMvc.perform(delete("/videos/{id}", this.existingId)
					    .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$").value("Vídeo removido com sucesso."));
	}	

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		ResultActions result =
				   this.mockMvc.perform(get("/videos/{id}", this.nonExistingId)
					   .accept(MediaType.APPLICATION_JSON));

				result.andExpect(status().isNotFound());
	}

	@Test
	public void findAllShouldReturnNotFoundNonExistingTitle() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/videos?search=" + this.nonExistingTitle)
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void updateShouldReturnNoFoundWhenIdExistis() throws Exception {

		String jsonBody = this.objectMapper.writeValueAsString(this.newVideoDTO);

		ResultActions result =
				   this.mockMvc.perform(put("/videos/{id}", this.nonExistingId)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnNoFoundWhenIdDoesNotExist() throws Exception {

		ResultActions result =
				   this.mockMvc.perform(delete("/videos/{id}", this.nonExistingId)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}
}