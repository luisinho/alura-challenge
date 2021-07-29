package br.com.alura.alurafix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.alura.alurafix.entities.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

}
