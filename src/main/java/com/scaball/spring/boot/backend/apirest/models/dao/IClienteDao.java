package com.scaball.spring.boot.backend.apirest.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scaball.spring.boot.backend.apirest.models.entity.Cliente;
import com.scaball.spring.boot.backend.apirest.models.entity.Region;

//se importa la clase CrudRepository<Nombre_entity, Tipo_dato_del_primarikey>
// para tener  los metodos de S,I,D,U a la base de datos ya automaticos
public interface IClienteDao extends JpaRepository<Cliente, Long>{
	
	// se crea la cosulta personalizada con la notacion @Query("from NombreEntidad")
	// si  hubiecen mas metodos para llamar a la entidad Region se crea otra interface DAO
	@Query("from Region")
	public  List<Region> finAllRegiones();

}
