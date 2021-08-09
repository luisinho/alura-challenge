package br.com.alura.aluraflix.tests.factory;

import br.com.alura.aluraflix.dto.VideoDTO;
import br.com.alura.aluraflix.entities.Category;
import br.com.alura.aluraflix.entities.Video;

public class VideoFactory {

	public static Video createVideo() {
		Video video = new Video(1l, "Teste Video", "TESTEVIDEO", "https://www.youtube.com/watch?v=4KLiqMe6b9U&ab_channel=C%C3%B3digoFonteTV");
		video.setCategoria(new Category(1l, "LIVRE", "blue"));
		return video;
	}

	public static VideoDTO criarVideoDTO() {
		Video video = createVideo();
		return new VideoDTO(video);
	}

	public static VideoDTO createVideoDTO(Long id) {
		VideoDTO dto = criarVideoDTO();
		dto.setId(id);
		return dto;
	}
}
