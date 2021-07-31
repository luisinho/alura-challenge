package br.com.alura.alurafix.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import br.com.alura.alurafix.entities.Categoria;

public class CategoriaDTO implements Serializable {	

	private static final long serialVersionUID = 1L;

	public Long id;

	@NotBlank(message = "Campo descrição requirido!")
	@Size(min = 5, max = 20, message = "O campo descrição deve ter entre 5 e 20 caracteres!")
	public String titulo;

	public String cor;

	private Instant createdAt;
	private Instant updatedAt;

	public CategoriaDTO() {

	}

	public CategoriaDTO(Categoria entity) {
		this.id = entity.getId();
		this.titulo = entity.getTitulo();
		this.cor = entity.getCor();
		this.createdAt = entity.getCreatedAt();
		this.updatedAt = entity.getUpdatedAt();
	}

	public CategoriaDTO(Long id, String titulo, String cor) {
		this.id = id;
		this.titulo = titulo;
		this.cor = cor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoriaDTO other = (CategoriaDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}