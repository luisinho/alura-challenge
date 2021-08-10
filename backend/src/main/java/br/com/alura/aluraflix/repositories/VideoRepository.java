package br.com.alura.aluraflix.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.alura.aluraflix.entities.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

	@Query("SELECT video FROM Video video WHERE video.titulo <> 'TESTEVIDEO' ")
	Page<Video> findAllPaged(Pageable pageable);

	@Query("SELECT video FROM Video video WHERE video.titulo LIKE %:title% AND video.titulo <> 'TESTEVIDEO' ")
	Page<Video> findTitle(@Param("title") String title,Pageable pageable);
}
