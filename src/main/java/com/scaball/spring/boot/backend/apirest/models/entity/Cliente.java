package com.scaball.spring.boot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name="clientes") // si la tabla se llama igual de la clase no es necesario @Table(name="clientes")
public class Cliente implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	// LA notacion @Column() se coloca si nombre del atributo no es el mismo de la columna de la tabla
	@NotEmpty(message="No puede estar vacio")// no puede ser vacio
	@Size(min=4, max=12,message="El tamaño iene que estar entre 4 y 12 ")
	@Column(nullable=false) 
	private String nombre;
	
	@NotEmpty(message="No puede estar vacio")// no puede ser vacio
	private String apellido;
	
	@NotEmpty(message="No puede estar vacio")// no puede ser vacio
	@Email(message="Formato de correo incorrecto")
	@Column(nullable=false,unique=false) 
	private String email;
	
	// LA notacion @Temporal(TemporalType.DATE) se coloca para convetir
	// el tipo de dato date de java a un  Date de sql
	@NotNull(message="La fecha no puede estar vacia")
	@Column(name="create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	
	private String foto;
	
	// SE crea la variable del tipo Regio para relacionar las tablas y se coloca la relacion de  tablas
	// como es de mucho a uno se coloca @ManyToOne  cada ves que se llame al metodo get de este atributo
	// mediante la carga perezosa ose solo se creara otro json ademas del de cliente 
	// cuando sea llamado el get
	// SE crea la llave foranea en la tabla clientes con @JoinColumn(name="nombreCampo_id")
	// Si se usa el FetchType.LAZY creara otros atributos al llamar la tabla region
	// se debeb excluir con la notacion @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
	// solo obetener losatributos propios de la clase Region
	@NotNull(message="La region no puede ser vacia")
	@ManyToOne(fetch=FetchType.LAZY)// carga perezosa : FetchType.LAZY
	@JoinColumn(name="region_id") // crea el campo de   llave foreanea y realiza el join entre entidades de tabla  clientes --> regiones
	@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
	private Region region;
	
	/* Creacion automatica de la fecha createAt con la notacion @PrePersist
	@PrePersist
	public void prePersist() {
		createAt = new Date();
	} */

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	

	private static final long serialVersionUID = 1L;

}
