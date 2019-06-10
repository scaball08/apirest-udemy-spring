package com.scaball.spring.boot.backend.apirest.auth;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.scaball.spring.boot.backend.apirest.models.services.UsuarioService;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private InfoAdicionalToken infoAdicionalToken;
	
	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;
	
	private Logger logger = LoggerFactory.getLogger(UsuarioService.class);

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		/*para dar acceso a nuestros clientes desde nuestro endpoint/oauth se usa el metodo 
		 * tokenKeyAccess("permitAll()") y se le agrega como parametro un string con el nombre del metodo "permitAll()"
		 * CON EL METODO checkTokenAccess("isAuthenticated()") validamos el token que enviamos en la cabecera
		 * para dar acceso 
		 * */
		security.tokenKeyAccess("permitAll()")
		.checkTokenAccess("isAuthenticated()"); 
		
		logger.info("se registran los metodos para dar acceso  la api ");
	}

	
	/*Metodo para configurar las aplicaciones qe se conectaran a nuestra api rest
	 * */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		/*para configurar un cliente del tipo ClientDetailsServiceConfigurer
		 * se debe indicar el tipo de almacenamiento del cliente en este caso inMemory()
		 * luego se indica  que aplicacion queremos dar permiso y registrar con withClient("angularapp")
		 * indicamos que podremos hacer (alcance) en nuestra api  como leer
		 *  datos y escribir datos con scopes("read","write") ,
		 *  Con authorizedGrantTypes("password","refresh_token") indicamos el tipo de autenticacion que tendremos 
		 *  ya sea por un usuario al hacer el login  o tambien si  solamente se quiere accesar al api 
		 *  con el authorizatioCode  para un redirecionamiento mediante un codigo de autoriacion
		 *  o implcito para acceder sin nigun tipo de codigo de autorizacion
		 *  con el refresh token  obtenemos un token renovado para poder seguir conectado a la api
		 *  con el TokenValiditySeconds indicamos el tiempo de valides de los token 
		 *  tanto accestoken como refreshtoken
		 * */
		clients.inMemory().withClient("angularapp")
		.secret(passwordEncoder.encode("12345"))
		.scopes("read","write")
		.authorizedGrantTypes("password","refresh_token")
		.accessTokenValiditySeconds(3600)
		.refreshTokenValiditySeconds(3600);
		
		logger.info("dado permisos al api para que se conecte a nuestro servicio rest ");
	}

	
	/*Metodo encargado de la autenticacion , generacion y validacion del token y poder acceder
	 * a las distitas enpoints de la aplicacion 
	 * */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		/* Primero se debe registrar el authenticationManager, segundo paso es registra al accestokenConverter
		 *  que se encarga de almacenar la informacion del usuario en el token siempre y cuando no sea sencible
		 *  tambien se encarga de decodificar para que el authenticationManager pueda relizar el proceso de autenticacion
		 * 
		 * */
		
		// Se crea el objeto del tipo TokenEnhancerChain
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		
		// se le agrega la lista de arrays para agregar la informacion adicional y la del accestokenconverter
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicionalToken,accessTokenConverter()));
		
		endpoints.authenticationManager(authenticationManager)
		.tokenStore(tokenStore())
		.accessTokenConverter(accessTokenConverter())
		.tokenEnhancer(tokenEnhancerChain); // pasamos la cadena tokenEnhancerChain
		logger.info("se agrega la infromacion del token ");
	}

	
	// se crea el metodo JwtTokenStore para configurar los endpoints
	@Bean
	public  JwtTokenStore tokenStore() {
		logger.info("configurar la clave publica y privada RSA ");
		
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		logger.info("configurar la clave publica y privada RSA ");
		
		JwtAccessTokenConverter jwtAccessTokenConverter =  new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey(JwtConfig.RSA_PRIVADA); // EL Firma  se agrega la llave privada
		jwtAccessTokenConverter.setVerifierKey(JwtConfig.RSA_PUBLICA);// El que verifica  se arega la llave publica
		return jwtAccessTokenConverter;
	}

	
}
