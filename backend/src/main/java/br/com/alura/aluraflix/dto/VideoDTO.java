package br.com.alura.aluraflix.dto;

import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import br.com.alura.aluraflix.entities.Video;

public class VideoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	@NotBlank(message = "Campo descrição requirido!")
	@Size(min = 5, max = 20, message = "O campo descrição deve ter entre 5 e 20 caracteres!")
	private String descricao;

	@NotBlank(message = "Campo título requirido!")
	@Size(min = 5, max = 20, message = "O campo título deve ter entre 5 e 20 caracteres!")
	private String titulo;

	@NotBlank(message = "Campo url requirido!")
	@Size(min = 24, max = 80, message = "O campo url deve ter entre 24 e 80 caracteres!")
	@Pattern(regexp="^((https)\\:\\/\\/)?(www\\.youtube\\.com|youtu\\.?be)\\/((watch\\?v=)?([a-zA-Z0-9]{11}))(&.*)*$", message = "Campo url inválido!")
	private String url;

	private CategoryDTO categoria;

	private Instant createdAt;
	private Instant updatedAt;

	public VideoDTO() {

	}

	public VideoDTO(Video entity) {
		this.id = entity.getId();
		this.descricao = entity.getDescricao();
		this.titulo = entity.getTitulo();
		this.url = entity.getUrl();
		this.createdAt = entity.getCreatedAt();
		this.updatedAt = entity.getUpdatedAt();

		if (entity.getCategoria() != null) {
			this.categoria = new CategoryDTO(entity.getCategoria());
		}
	}

	public VideoDTO(Long id, String descricao, String titulo, String url) {
		this.id = id;
		this.descricao = descricao;
		this.titulo = titulo;
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public CategoryDTO getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoryDTO categoria) {
		this.categoria = categoria;
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
		VideoDTO other = (VideoDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
}