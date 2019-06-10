package com.scaball.spring.boot.backend.apirest.auth;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.scaball.spring.boot.backend.apirest.models.services.UsuarioService;

// clase para configurar los permisos a los endpoints
// tambien se puede hacer la misma funcion agregando la notacion 
// @EnableGlobalMethodSecurity(securedEnabled=true) en la clase 
// SpringSecurityConfig que hereda de WebSecurityConfigurerAdapter
// y luego en el controlador en cada metodo con enpoint argregar la notacion @Secured("ROLE_nombreRol")
// si hay mas de un rol se encierran entre llaves{} y separador pos coma(,) ademas
// el nombre del rol debe tenerantepuestalapalabra en mayuzcula 'ROLE_'

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends  ResourceServerConfigurerAdapter{
	
	private Logger logger = LoggerFactory.getLogger(UsuarioService.class);

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// Se dan los permisos para los recursos de nuestro servidor  ya sean arhivos o endpoints de nuestra api
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/clientes","/api/clientes/page/**","/api/uploads/img/**","/images/**").permitAll()
		/*.antMatchers(HttpMethod.GET,"/api/clientes/{id}").hasAnyRole("USER","ADMIN")
		.antMatchers(HttpMethod.POST,"/api/clientes/upload").hasAnyRole("USER","ADMIN")
		.antMatchers(HttpMethod.POST,"/api/clientes").hasRole("ADMIN")
		.antMatchers("/api/clientes/**").hasRole("ADMIN")*/
		.anyRequest().authenticated()
		.and().cors().configurationSource(corsConfigurationSource()); // metodos para agregar la configuracion CORS que esta en el metodo creado corsConfigurationSource
		
		logger.info("Adminitrar endpoints mediante su rol");
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
		CorsConfiguration corsConfi = new CorsConfiguration();
		
		// CON el metodo setAllowedOrigins(lista_de_dominio) indicamos mediante una lista de arrays los dominio qeu queremos permitir a nuestra api
		// la lista de array la aregamos con la clase helper  Arrays.asList("http://localhost:4200", "http://localhost:4200")
		corsConfi.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // configuramos el dominio al que queremos ingresar
		corsConfi.setAllowedMethods(Arrays.asList("GET","POST","PUT","OPTIONS","DELETE"));
		corsConfi.setAllowCredentials(true); // se habilitan las credenciales 
		corsConfi.setAllowedHeaders(Arrays.asList("Content-Type","Authorization")); // se agregan los heder que enviaremos si  no se agregar el setAllowedHeaders(arrays de string con los atributos)
		
		// SE crea un objeto de tipo UrlBasedCorsConfigurationSource para configurar
		//todas las rutas con la configuracion de corsConfi
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// Mediante el metodo
		source.registerCorsConfiguration("/**", corsConfi);
		
		// una ves configurado con el cors a todas las rutas lo retornamos
	  return source;
	}
	
	// para registrar la configuracion en  el auth2 y en el login 
	// crearemos un metodo del tipo FilterRegistrationBean<Tipo_de_dato_a_registrar> 
	// Se registra la configuracion  dontro del stask del conjunto de filtros que maneja Springframework
	// y asi queda configurado para spring Security como para Oauth2
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		// se crea una instacia  del tipo  FilterRegistrationBean<CorsFilter>
		// en la que su constructor recive una instacia del tipo CorsFilter y en el parametro del contructor  
		// recibe la configuracion  del metodo que creamos corsConfigurationSource()
		 FilterRegistrationBean<CorsFilter> bean  = new  FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
	    
		 // SE da un orden de prioridad con setOrder(Ordered.HIGHEST_PRECEDENCE) entre mas baja mayor es la prioridad
	     bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
	     
	     return bean;
	}

}
