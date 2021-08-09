package br.com.alura.aluraflix.tests.factory;

import br.com.alura.aluraflix.dto.CategoryDTO;
import br.com.alura.aluraflix.entities.Category;

public class CategoryFactory {

	public static Category createCategory() {
		Category category = new Category(1l, "LIVRE", "blue");
		return category;
	}

	public static CategoryDTO createCategoryDTO() {
		Category category = createCategory();
		return new CategoryDTO(category);
	}

	public static CategoryDTO createCategoryDTO(Long id) {
		CategoryDTO dto = createCategoryDTO();
		dto.setId(id);
		return dto;
	}
}
