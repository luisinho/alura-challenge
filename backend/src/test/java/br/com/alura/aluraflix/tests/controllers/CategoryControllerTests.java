package br.com.alura.aluraflix.tests.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

	private String username;

	private String password;

	private Long existingId;

	private Long nonExistingId;

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;

	@BeforeEach
	void setUp() throws Exception {

		this.username = "admin@alura.com.br";

		this.password = "12345678";

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

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
		   this.mockMvc.perform(get("/categorias")
				        .header("Authorization", "Bearer " + accessToken)
			            .contentType(MediaType.APPLICATION_JSON)
			            .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
	}

	@Test
	public void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
		   this.mockMvc.perform(get("/categorias/{id}", this.existingId)
				   .header("Authorization", "Bearer " + accessToken)
			       .contentType(MediaType.APPLICATION_JSON)
			       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
	}

	@Test
	public void getShouldReturnCategoryWithVideoWhenIdExists() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
		   this.mockMvc.perform(get("/categorias/{id}/videos", this.existingId)
				   .header("Authorization", "Bearer " + accessToken)
			       .contentType(MediaType.APPLICATION_JSON)
			       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
	}

	@Test
	public void saveShouldReturnCategoryDTOWhenCreated() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		String jsonBody = this.objectMapper.writeValueAsString(this.newCategoryDTO);

		ResultActions result =
				   this.mockMvc.perform(post("/categorias")
						   .header("Authorization", "Bearer " + accessToken)
					       .content(jsonBody)
					       .contentType(MediaType.APPLICATION_JSON)
					       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
	}

	@Test
	public void updateShouldReturnCategoryDTOWhenIdExistis() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		String jsonBody = this.objectMapper.writeValueAsString(this.newCategoryDTO);

		String expectedTitulo = this.newCategoryDTO.getTitulo();

		String expectedCor = this.newCategoryDTO.getCor();

		ResultActions result =
				   this.mockMvc.perform(put("/categorias/{id}", this.existingId)
						   .header("Authorization", "Bearer " + accessToken)
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

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
				   this.mockMvc.perform(delete("/categorias/{id}", this.existingId)
						   .header("Authorization", "Bearer " + accessToken)
					       .contentType(MediaType.APPLICATION_JSON)
					       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$").value("Categoria deletada com sucesso."));
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
				   this.mockMvc.perform(get("/categorias/{id}", this.nonExistingId)
						   .header("Authorization", "Bearer " + accessToken)
					       .accept(MediaType.APPLICATION_JSON));

				result.andExpect(status().isNotFound());
	}

	@Test
	public void getShouldCategoryWithVideoReturnNotFoundWhenIdDoesNotExists() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
		   this.mockMvc.perform(get("/categorias/{id}/videos", this.nonExistingId)
				   .header("Authorization", "Bearer " + accessToken)
			       .contentType(MediaType.APPLICATION_JSON)
			       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void updateShouldReturnNoFoundWhenIdExistis() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		String jsonBody = this.objectMapper.writeValueAsString(this.newCategoryDTO);

		ResultActions result =
				   this.mockMvc.perform(put("/categorias/{id}", this.nonExistingId)
						   .header("Authorization", "Bearer " + accessToken)
					       .content(jsonBody)
					       .contentType(MediaType.APPLICATION_JSON)
					       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnNoFoundWhenIdDoesNotExist() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
				   this.mockMvc.perform(delete("/categorias/{id}", this.nonExistingId)
						   .header("Authorization", "Bearer " + accessToken)
					       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
		
	}

	private String obtainAccessToken(String username, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", this.clientId);
		params.add("username", this.username);
		params.add("password", this.password);

		ResultActions result = mockMvc
				.perform(post("/oauth/token").params(params).with(httpBasic(this.clientId, this.clientSecret))
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}
}