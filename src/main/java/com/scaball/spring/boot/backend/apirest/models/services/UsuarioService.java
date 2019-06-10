package com.scaball.spring.boot.backend.apirest.models.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scaball.spring.boot.backend.apirest.models.dao.IUsuarioDao;
import com.scaball.spring.boot.backend.apirest.models.entity.Usuario;


@Service
public class UsuarioService implements UserDetailsService , IUsuarioService {

	@Autowired
	private IUsuarioDao usuarioDao;
	
	private Logger logger = LoggerFactory.getLogger(UsuarioService.class);
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioDao.findByUsername(username);
		
		if (usuario==null) {
			logger.error("Error en el login: No existe el usuario en el sistema!");
			
			throw new UsernameNotFoundException("Error en el login: No existe el usuario '"+ username +"'en el sistema!");
		}
		
		
		
		/* SE crear una variable de colenccion List<GrantedAuthority> que recibira la lista de roles
		 * pero antes  se debe transformar los roles que nos retorna de usuario.getRoles() a una lista
		 * de SimpleGrantedAuthority(nombreRol) utilizando el metodo .stream() cre un flujo de collecion
		 * luego con .map(role-> convertimos cada objeto colecion en una instacia de 
		 * new SimpleGrantedAuthority(role.getNombre())  y como punto final se deben transformar a una 
		 * lista con el metodo .collect(Collectors.toList()) ademas si queremos mostrar el nombre
		 * de cada rol usamos la exprension lamda .peek(authority-> logger.info("mensaje"))
		 * */
		List<GrantedAuthority> authorities = usuario.getRoles()
				.stream()
				.map(role-> new SimpleGrantedAuthority(role.getNombre()) )
				.peek(authority-> logger.info("Role: " + authority.getAuthority()))
				.collect(Collectors.toList());
		
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true, authorities);
	}

	@Override
	@Transactional(readOnly=true)
	public Usuario findByUsername(String username) {
		
		return usuarioDao.findByUsername(username);
	}

}
