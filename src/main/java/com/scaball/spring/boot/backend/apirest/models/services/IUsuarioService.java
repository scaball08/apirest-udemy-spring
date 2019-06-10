package com.scaball.spring.boot.backend.apirest.models.services;

import com.scaball.spring.boot.backend.apirest.models.entity.Usuario;

public interface IUsuarioService {
	
	
	public Usuario findByUsername(String username);

}
