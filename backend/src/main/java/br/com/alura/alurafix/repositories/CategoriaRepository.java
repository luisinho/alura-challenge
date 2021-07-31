package br.com.alura.alurafix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.alurafix.entities.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
