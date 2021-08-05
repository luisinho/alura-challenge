package br.com.alura.alurafix.tests.repositories;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.alura.alurafix.entities.Categoria;
import br.com.alura.alurafix.entities.Video;
import br.com.alura.alurafix.repositories.VideoRepository;
import br.com.alura.alurafix.tests.factory.VideoFactory;

@DataJpaTest
public class VideoRepositoryTests {

	@Autowired
	private VideoRepository videoRepository;

	private long existingId;
	private long nonExistingId;
	private long countTotalProduct;
	private long countPCGamerProducts;
	private Categoria categoryFree;
	private Categoria nonExistingCategory;
	private long countVideos;
	private Pageable pageable;

	@BeforeEach
	void setUp() throws Exception {

		this.countVideos = 1;
		this.pageable = PageRequest.of(0, 10);
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

		Video video = VideoFactory.criarVideo();
		video.setId(null);

		video = this.videoRepository.save(video);

		Optional<Video> result = this.videoRepository.findById(video.getId());

		Assertions.assertNotNull(video.getId());

		Assertions.assertTrue(result.isPresent());

		Assertions.assertSame(result.get(), video);
	}

	@Test
	public void findShouldReturnAllVideo() {
		
		Page<Video> result = this.videoRepository.findAll(pageable);
		
		Assertions.assertFalse(result.isEmpty());
		
	}

	@Test
	public void findShouldReturnVideoWhenTituloNotEmpty() {
		
		String titleSearch = "React";
		
		Optional<Video> result = this.videoRepository.findByTitulo(titleSearch);
		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void findShouldReturnEmptyWhenTituloNotExists() {
		
		String titleSearch = "React 3";
		
		Optional<Video> result = this.videoRepository.findByTitulo(titleSearch);
		
		Assertions.assertTrue(result.isEmpty());
	}
}