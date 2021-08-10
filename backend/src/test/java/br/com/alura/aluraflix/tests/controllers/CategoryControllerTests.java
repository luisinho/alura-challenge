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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.aluraflix.dto.CategoryDTO;
import br.com.alura.aluraflix.exceptions.RegisterNotFoundException;
import br.com.alura.aluraflix.services.CategoryService;
import br.com.alura.aluraflix.tests.factory.CategoryFactory;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CategoryService categoryService;

	@Autowired
	private ObjectMapper objectMapper;

	private CategoryDTO  newCategoryDTO;

	private CategoryDTO existingCategoryDTO;

	private PageImpl<CategoryDTO> page;

	private Long existingId;

	private Long nonExistingId;	

	@BeforeEach
	void setUp() throws Exception {

		this.existingId = 1l;

		this.nonExistingId = -1l;

		this.newCategoryDTO = CategoryFactory.createCategoryDTO(null);

		this.existingCategoryDTO = CategoryFactory.createCategoryDTO(this.existingId);

		this.page = new PageImpl<>(Arrays.asList(this.existingCategoryDTO));

		when(this.categoryService.findAllPaged(any())).thenReturn(this.page);

		when(this.categoryService.findById(this.existingId)).thenReturn(this.existingCategoryDTO);
		
		when(this.categoryService.getVideoByCategory(this.existingId)).thenReturn(this.existingCategoryDTO);

		when(this.categoryService.save(newCategoryDTO)).thenReturn(this.existingCategoryDTO);

		when(this.categoryService.update(eq(this.existingId), any())).thenReturn(this.existingCategoryDTO);

		doNothing().when(this.categoryService).delete(this.existingId);
		
		when(this.categoryService.findById(this.nonExistingId)).thenThrow(RegisterNotFoundException.class);

		when(this.categoryService.getVideoByCategory(this.nonExistingId)).thenThrow(RegisterNotFoundException.class);

		when(this.categoryService.update(eq(this.nonExistingId), any())).thenThrow(RegisterNotFoundException.class);

		doThrow(RegisterNotFoundException.class).when(this.categoryService).delete(this.nonExistingId);
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/categorias")
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
	}

	@Test
	public void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/categorias/{id}", this.existingId)
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
	}

	@Test
	public void getShouldReturnCategoryWithVideoWhenIdExists() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/categorias/{id}/videos", this.existingId)
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
	}

	@Test
	public void saveShouldReturnCategoryDTOWhenCreated() throws Exception {

		String jsonBody = this.objectMapper.writeValueAsString(this.newCategoryDTO);

		ResultActions result =
				   this.mockMvc.perform(post("/categorias")
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
	}

	@Test
	public void updateShouldReturnCategoryDTOWhenIdExistis() throws Exception {

		String jsonBody = this.objectMapper.writeValueAsString(this.newCategoryDTO);

		String expectedTitulo = this.newCategoryDTO.getTitulo();

		String expectedCor = this.newCategoryDTO.getCor();

		ResultActions result =
				   this.mockMvc.perform(put("/categorias/{id}", this.existingId)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
		result.andExpect(jsonPath("$.titulo").value(expectedTitulo));
		result.andExpect(jsonPath("$.cor").value(expectedCor));
	}

	@Test
	public void deleteShouldReturnMessageWhenIdExistis() throws Exception {

		ResultActions result =
				   this.mockMvc.perform(delete("/categorias/{id}", this.existingId)
					    .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$").value("Categoria deletada com sucesso."));
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		ResultActions result =
				   this.mockMvc.perform(get("/categorias/{id}", this.nonExistingId)
					   .accept(MediaType.APPLICATION_JSON));

				result.andExpect(status().isNotFound());
	}

	@Test
	public void getShouldCategoryWithVideoReturnNotFoundWhenIdDoesNotExists() throws Exception {

		ResultActions result =
		   this.mockMvc.perform(get("/categorias/{id}/videos", this.nonExistingId)
			   .contentType(MediaType.APPLICATION_JSON)
			   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void updateShouldReturnNoFoundWhenIdExistis() throws Exception {

		String jsonBody = this.objectMapper.writeValueAsString(this.newCategoryDTO);

		ResultActions result =
				   this.mockMvc.perform(put("/categorias/{id}", this.nonExistingId)
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnNoFoundWhenIdDoesNotExist() throws Exception {

		ResultActions result =
				   this.mockMvc.perform(delete("/categorias/{id}", this.nonExistingId)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}
}