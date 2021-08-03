package br.com.alura.alurafix.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.alura.alurafix.entities.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

	@Query("SELECT video FROM Video video WHERE (COALESCE(:search) IS NOT NULL OR video.titulo = :search)")
	List<Video> listarVideo(@Param("search") String search);

	Optional<Video> findByTitulo(String search);
}
