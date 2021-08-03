package br.com.alura.alurafix.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.alura.alurafix.entities.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	@Query("SELECT cat FROM Categoria cat LEFT JOIN cat.videos WHERE cat.id = :categoriaId")
	public Categoria obterCategoriaVideos(@Param("categoriaId") Long categoriaId);

	@Query("SELECT COUNT(cat.id) FROM Categoria cat INNER JOIN cat.videos video WHERE video.categoria.id = :categoriaId")
	public long countVideoPorCategoria(@Param("categoriaId") Long categoriaId);

	public long countByTituloIgnoreCase(String titulo);

	public Optional<Categoria> findByTituloIgnoreCase(String titulo);
}
