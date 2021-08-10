package br.com.alura.aluraflix.tests.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.alura.aluraflix.entities.Video;
import br.com.alura.aluraflix.repositories.VideoRepository;
import br.com.alura.aluraflix.tests.factory.VideoFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class VideoRepositoryTests {

	@Autowired
	private VideoRepository videoRepository;

	private long existingId;

	private long nonExistingId;

	private long countVideos;

	private Pageable pageable;

	private String changeDescription;	

	@BeforeEach
	void setUp() throws Exception {

		this.existingId = 1;

		this.nonExistingId = -1;

		this.countVideos = 1;

		this.changeDescription = "Teste Video1";

		this.pageable = PageRequest.of(0, 10);
	}

	@Test
	public void findShouldReturnAllVideo() {

		Page<Video> result = this.videoRepository.findAll(pageable);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertTrue(result.getTotalElements() >= this.countVideos);
	}

	@Test
	public void findShouldReturnVideoWhenTitleExists() {
		
		String titleSearch = "TESTEVIDEO";
		
		Page<Video> result = this.videoRepository.findTitle(titleSearch, this.pageable);

		Assertions.assertNotNull(result.getContent());
	}

	@Test
	public void findShouldReturnVideoWhenIdExists() {

		Optional<Video> video = this.videoRepository.findById(this.existingId);

		Assertions.assertFalse(video.isEmpty());
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

		Video video = VideoFactory.createVideo();
		video.setId(null);

		video = this.videoRepository.save(video);

		Optional<Video> result = this.videoRepository.findById(video.getId());

		Assertions.assertNotNull(video.getId());

		Assertions.assertTrue(result.isPresent());

		Assertions.assertSame(result.get(), video);
	}
	
	@Test
	public void updateShouldReturnVideoWhenIdExistis() throws Exception {
		
		Video video = VideoFactory.createVideo();
		video.setDescricao(this.changeDescription);
		
		video = this.videoRepository.save(video);
		
		Optional<Video> result = this.videoRepository.findById(video.getId());
		
		Assertions.assertNotNull(result.get());
		
		Assertions.assertSame(result.get().getDescricao(), this.changeDescription);		
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		this.videoRepository.deleteById(this.existingId);

		Optional<Video> result = this.videoRepository.findById(this.existingId);

		Assertions.assertFalse(result.isPresent());
	}	

	@Test
	public void findShouldReturnEmptyWhenTitleNotExists() {

		String titleSearch = "_____|";

		Page<Video> result = this.videoRepository.findTitle(titleSearch, this.pageable);

		Assertions.assertTrue(result.getContent().isEmpty());
	}

	@Test
	public void findShouldReturnEmptyWhenIdNotExists() {

		Optional<Video> result = this.videoRepository.findById(this.nonExistingId);

		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	public void updateShouldThrowExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(Exception.class, () -> {

			Video video = this.videoRepository.getById(this.nonExistingId);

			video.setDescricao(this.changeDescription);

			video = this.videoRepository.save(video);
		});
	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {

			this.videoRepository.deleteById(this.nonExistingId);
		});
	}
}