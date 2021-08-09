package br.com.alura.aluraflix.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.alura.aluraflix.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("SELECT cat FROM Category cat LEFT JOIN cat.videos WHERE cat.id = :categoriaId")
	public Category getVideoByCategory(@Param("categoriaId") Long categoriaId);

	@Query("SELECT COUNT(cat.id) FROM Category cat INNER JOIN cat.videos video WHERE video.categoria.id = :categoriaId")
	public long countVideoPorCategoria(@Param("categoriaId") Long categoriaId);

	public long countByTituloIgnoreCase(String titulo);

	public Optional<Category> findByTituloIgnoreCase(String titulo);
}
