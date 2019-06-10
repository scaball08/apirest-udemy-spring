package com.scaball.spring.boot.backend.apirest.auth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.scaball.spring.boot.backend.apirest.models.entity.Usuario;
import com.scaball.spring.boot.backend.apirest.models.services.IUsuarioService;
import com.scaball.spring.boot.backend.apirest.models.services.UsuarioService;

/*Clase para agregar  informacion adicional al token en el carga util
 * Tambien se necesita registrarse en el servidor de autenticacion o AuthorizationServer
 * en el metodo donde estan el configure(AuthorizationServerEndpointsConfigurer)
 * */
@Component
public class InfoAdicionalToken implements TokenEnhancer {
	
	@Autowired
	private IUsuarioService usuarioService;
	
	private Logger logger = LoggerFactory.getLogger(UsuarioService.class);

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		logger.info("Agregando informacion adicional");
		
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		
		// se crea un objeto Map cuyo valor es generico(Object)  e implementa de HasMap donde se le
		// agregaran los valores adicionales a nuestro troken
		
		Map<String,Object> info = new HashMap<>();
		
		info.put("info_adicional", "hola que tal".concat(authentication.getName()));
		
		info.put("nombre", usuario.getNombre() );
		info.put("apellido", usuario.getApellido() );
		info.put("email", usuario.getEmail());
		
		// como el objeto OAuth2AccessToken no tiene metodos para setearle valores 
		// se debe castear a una clase deriva la "DefaultOAuth2AccessToken" y con 
		// setAdditionalInformation(info) se le agrega el objeto map con nuestros valores 
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		return accessToken;
	}

}
