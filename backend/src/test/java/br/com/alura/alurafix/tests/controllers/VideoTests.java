package br.com.alura.alurafix.tests.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.alurafix.dto.VideoDTO;
import br.com.alura.alurafix.services.VideoService;
import br.com.alura.alurafix.tests.factory.VideoFactory;

@SpringBootTest
@AutoConfigureMockMvc
public class VideoTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VideoService videoService;

	@Autowired
	private ObjectMapper objectMapper;

	private VideoDTO  newVideoDTO;

	private VideoDTO existingVideoDTO;
	
	private Long existingId;

	@BeforeEach
	void inicializar() throws Exception {

		existingId = 1l;

		this.newVideoDTO = VideoFactory.criarVideoDTO(null);

		this.existingVideoDTO = VideoFactory.criarVideoDTO(this.existingId);

		when(this.videoService.criarVideo(any())).thenReturn(this.existingVideoDTO);
	}

	@Test
	public void retornarProdutoDTOQuandoCriado() throws Exception {

		String jsonBody = this.objectMapper.writeValueAsString(this.newVideoDTO);

		ResultActions result =
				   this.mockMvc.perform(post("/videos")
					   .content(jsonBody)
					   .contentType(MediaType.APPLICATION_JSON)
					   .accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
	}
}