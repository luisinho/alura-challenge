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

import br.com.alura.aluraflix.entities.Category;
import br.com.alura.aluraflix.repositories.CategoryRepository;
import br.com.alura.aluraflix.tests.factory.CategoryFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class CategoryRepositoryTests {

	@Autowired
	private CategoryRepository categoriaRepository;

	private long existingId;
	private long nonExistingId;
	private String title;
	private String changeColor;
	private long countCategories;
	private long countVideos;
	private Pageable pageable;

	@BeforeEach
	void setUp() throws Exception {

		this.existingId = 1;
		this.nonExistingId = -1;
		this.title = "LIVRE";
		this.changeColor = "#00FF00";
		this.countCategories = 1;
		this.countVideos = 1;
		this.pageable = PageRequest.of(0, 10);
	}

	@Test
	public void findShouldReturnAllCategory() {

		Page<Category> result = this.categoriaRepository.findAll(pageable);

		Assertions.assertFalse(result.isEmpty());

		Assertions.assertTrue(result.getTotalElements() >= this.countCategories);
	}

	@Test
	public void findShouldReturnCategoryWhenIdExists() {

		Optional<Category> category = this.categoriaRepository.findById(this.existingId);

		Assertions.assertFalse(category.isEmpty());
	}

	@Test
	public void getShouldReturnCategoryWithVideoWhenIdExists() {

		Category category = this.categoriaRepository.getVideoByCategory(this.existingId);

		Assertions.assertNotNull(category);
		Assertions.assertNotNull(category.getId());
		Assertions.assertNotNull(category.getVideos());
		Assertions.assertTrue(category.getVideos().size() >= this.countVideos);
	}

	@Test
	public void getShouldReturnCategoryWhenTitleExists() {

		Optional<Category> result = this.categoriaRepository.findByTituloIgnoreCase(this.title);

		Assertions.assertNotNull(result.get());
		Assertions.assertEquals(result.get().getTitulo(), this.title);
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

		Category category = CategoryFactory.createCategory();
		category.setId(null);

		category = this.categoriaRepository.save(category);

		Optional<Category> result = this.categoriaRepository.findById(category.getId());

		Assertions.assertNotNull(category.getId());

		Assertions.assertTrue(result.isPresent());

		Assertions.assertSame(result.get(), category);
	}

	@Test
	public void updateShouldReturnCategoryWhenIdExistis() throws Exception {

		Category category = CategoryFactory.createCategory();
		category.setCor(this.changeColor);

		category = this.categoriaRepository.save(category);

		Optional<Category> result = this.categoriaRepository.findById(category.getId());

		Assertions.assertNotNull(result.get());

		Assertions.assertSame(result.get().getCor(), this.changeColor);
	}

	@Test
	public void updateShouldThrowExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(Exception.class, () -> {

			Category category = this.categoriaRepository.getById(this.nonExistingId);

			category.setCor(this.changeColor);

			category = this.categoriaRepository.save(category);
		});
	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {

			this.categoriaRepository.deleteById(this.nonExistingId);
		});
	}

	@Test
	public void deleteShouldThrowExceptionWhenVideoExists() {
 
		Assertions.assertThrows(Exception.class, () -> {

			this.categoriaRepository.deleteById(this.existingId);
			this.categoriaRepository.flush();
		});
	}
}