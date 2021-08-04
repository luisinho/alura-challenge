package br.com.alura.alurafix.tests.factory;

import br.com.alura.alurafix.dto.VideoDTO;
import br.com.alura.alurafix.entities.Categoria;
import br.com.alura.alurafix.entities.Video;

public class VideoFactory {

	public static Video criarVideo() {
		Video video = new Video(1l, "Video alura react", "React", "https://www.youtube.com/watch?v=4KLiqMe6b9U&ab_channel=C%C3%B3digoFonteTV");
		video.setCategoria(new Categoria(1l, "LIVRE", "blue"));
		return video;
	}

	public static VideoDTO criarVideoDTO() {
		Video video = criarVideo();
		return new VideoDTO(video);
	}

	public static VideoDTO criarVideoDTO(Long id) {
		VideoDTO dto = criarVideoDTO();
		dto.setId(id);
		return dto;
	}
}
