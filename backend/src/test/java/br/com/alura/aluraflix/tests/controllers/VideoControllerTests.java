package br.com.alura.aluraflix.tests.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

	private String username;

	private String password;

	private Long existingId;
	
	private Long nonExistingId;
	
	private String existingTitle;

	private String nonExistingTitle;

	private PageRequest pageRequest;

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;

	@BeforeEach
	void setUp() throws Exception {		

		this.username = "admin@alura.com.br";

		this.password = "12345678";

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

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
		   this.mockMvc.perform(get("/videos")
				   .header("Authorization", "Bearer " + accessToken)
			       .contentType(MediaType.APPLICATION_JSON)
			       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
	}	

	@Test
	public void findByIdShouldReturnVideoDTOWhenIdExists() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
		   this.mockMvc.perform(get("/videos/{id}", this.existingId)
				   .header("Authorization", "Bearer " + accessToken)
			       .contentType(MediaType.APPLICATION_JSON)
			       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(this.existingId));
	}

	@Test
	public void findByIdShouldReturnVideoDTOWhenTitleExists() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
				   this.mockMvc.perform(get("/videos?search=" + this.existingTitle)
						   .header("Authorization", "Bearer " + accessToken)
					       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
	}

	@Test
	public void saveShouldReturnVideoDTOWhenCreated() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		String jsonBody = this.objectMapper.writeValueAsString(this.newVideoDTO);

		ResultActions result =
				   this.mockMvc.perform(post("/videos")
						   .header("Authorization", "Bearer " + accessToken)
					       .content(jsonBody)
					       .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
	}

	@Test
	public void updateShouldReturnVideoDTOWhenIdExistis() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		String jsonBody = this.objectMapper.writeValueAsString(this.newVideoDTO);

		String expectedTitulo = this.newVideoDTO.getTitulo();

		String expectedDescricao = this.newVideoDTO.getDescricao();

		ResultActions result =
				   this.mockMvc.perform(put("/videos/{id}", this.existingId)
						   .header("Authorization", "Bearer " + accessToken)
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

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
				   this.mockMvc.perform(delete("/videos/{id}", this.existingId)
						   .header("Authorization", "Bearer " + accessToken)
					       .contentType(MediaType.APPLICATION_JSON)
					       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$").value("Vídeo removido com sucesso."));
	}	

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
				   this.mockMvc.perform(get("/videos/{id}", this.nonExistingId)
						   .header("Authorization", "Bearer " + accessToken)
					       .accept(MediaType.APPLICATION_JSON));

				result.andExpect(status().isNotFound());
	}

	@Test
	public void findAllShouldReturnNotFoundNonExistingTitle() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		ResultActions result =
		   this.mockMvc.perform(get("/videos?search=" + this.nonExistingTitle)
				   .header("Authorization", "Bearer " + accessToken)
			       .contentType(MediaType.APPLICATION_JSON)
			       .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void updateShouldReturnNoFoundWhenIdExistis() throws Exception {

		String accessToken = this.obtainAccessToken(this.username, this.password);

		String jsonBody = this.objectMapper.writeValueAsString(this.newVideoDTO);

		ResultActions result =
				   this.mockMvc.perform(put("/videos/{id}", this.nonExistingId)
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
				   this.mockMvc.perform(delete("/videos/{id}", this.nonExistingId)
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