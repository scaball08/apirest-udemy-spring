package com.scaball.spring.boot.backend.apirest.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scaball.spring.boot.backend.apirest.models.entity.Cliente;
import com.scaball.spring.boot.backend.apirest.models.entity.Region;
import com.scaball.spring.boot.backend.apirest.models.services.IClienteService;
import com.scaball.spring.boot.backend.apirest.models.services.IUploadFileService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	// se colca la anotacion @Autowired para inyectar la interfaz IClienteService que implementa 
	// en la clase ClienteServiceImpl vendria siendo un subtipo de IClienteService
	// spring buscara la primera clase que implemente ClienteServiceImpl  como solo la implementa 
	// ClienteServiceImpl sera esa pero si hay mas de coloca el nombre de la clase @Qualifier
	@Autowired
	private IClienteService clienteService;
	
	@Autowired 
	private IUploadFileService uploadService;
	
	private final Logger log = LoggerFactory.getLogger(ClienteRestController.class); 
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page){
		Pageable pageable =  PageRequest.of(page, 4);
		return clienteService.findAll(pageable);
	}
	
	@Secured({"ROLE_USER","ROLE_ADMIN"})
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable long id) {
		
		Cliente cliente = null;
		Map<String,Object> response = new HashMap<String,Object>();
		
		 try {
			 cliente =  clienteService.findById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al consultar cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
	        return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);// 404
			
		}
		
		if (cliente==null) {
			response.put("mensaje", "El Cliente ID: ".concat(Long.toString(id)).concat(" no existe en la base de datos"));
        return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);// 404
		}
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	} 
	
	
	
	
  // COMO le estamos enviando los datos del cliente en el body de la peticion
  // tenemos que indicarselo al metodo con @RequestBody en los parametros.
  //PARA enviar el estatus(codigo) de la respuesta lo haremos con @ResponseStatus(HttpStatus.CREATED)
	
	//PARA habilitar las notaciones de validacion que se coloco en la entidad:
	//@NotEmpty// no puede ser vacio, @Email ,@Column(nullable=false,unique=true) 
	// se debe colocar la  notacion @Valid ants del @RequestBody y 
	// ademas como parametros se  agregar el objeto 'BindingResult' que inyecta al metodo
	//  los mensajes de error de las validaciones que utilizaremos para validar si hay errores
	//  con el result.hasErrors()  ademas siempre va  antes del @PathVariable
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente,BindingResult result) {
		//Se crea la fecha de creacion con:
		//cliente.setCreateAt(new Date());
 //PERO se creara en la entidad de manera automatica con el @PrePersit ver clase Cliente
		
		Cliente clienteNew = null;
		Map<String,Object> response = new HashMap<String,Object>();
		
		
		// VALIDA se hay errores en las validaciones colocadas en el entidad como (@NotEmpty): 
		// if (result.hasErrors())
		if (result.hasErrors()) {
		//	SI hay errores retornamos el ResponseEntity con el objeto Map<String,Object>
		// con la lista de tipo String de los errores de los campos:
			
			//FORMA 1
			//List<String> errors =  new ArrayList<String>();// lista con mensajes de error de campos
		  //ITERAMOS la lista de  errores de campos con el forEach	
		/*	for (FieldError err : result.getFieldErrors()) {
				errors.add("El campo '" + err.getField()+ "' " + err.getDefaultMessage());
				
			} */
			
			//FORMA 2
			List<String> errors = result.getFieldErrors()
					.stream() //EL EQUIVALENTE a filter() de angular que itera una lista de objetos
			        .map(err->"El campo '" + err.getField()+ "' " + err.getDefaultMessage()) // Cada FieldError lo convertimos a un string
			        .collect(Collectors.toList()); // lo trasnformamos a una lista List<String>
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);// 404			
			
		}
		
		
		try {
			
			clienteNew =  clienteService.save(cliente);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
	        return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);// 404			
			
			
		}
		
		response.put("mesanje", "El Cliente se ha creado con exito");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);  // 201 created
	}
	
	@Secured({"ROLE_ADMIN"})
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente,BindingResult result ,@PathVariable long id ) {
		Map<String,Object> response = new HashMap<String,Object>();
		Cliente clienteActual = null;
		Cliente clienteUpdate = null;
		
		if (result.hasErrors()) {
			
				List<String> errors = result.getFieldErrors()
						.stream() //EL EQUIVALENTE a filter() de angular que itera una lista de objetos
				        .map(err->"El campo '" + err.getField()+ "' " + err.getDefaultMessage()) // Cada FieldError lo convertimos a un string
				        .collect(Collectors.toList()); // lo trasnformamos a una lista List<String>
				
				response.put("errors", errors);
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);// 404			
				
			}
		
		
		try {
			clienteActual = clienteService.findById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al consultar cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
	        return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);// 404
			
		}
		if (clienteActual==null) {
			response.put("mensaje", "No se pudo editar el Cliente ID: ".concat(Long.toString(id)).concat(" no existe en la base de datos"));
        return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);// 404
		}
		
		
		clienteActual.setNombre(cliente.getNombre());
		clienteActual.setApellido(cliente.getApellido());
		clienteActual.setEmail(cliente.getEmail());
		clienteActual.setRegion(cliente.getRegion());
		
		
         try {
			
        	 clienteUpdate = clienteService.save(clienteActual);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
	        return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);// 404			
			
		}
		
        response.put("mesanje", "El Cliente se ha actualizado con exito");
 		response.put("cliente", clienteUpdate);
 		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);  // 201 created

	}
	
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {
		
		
		Map<String,Object> response = new HashMap<>();
		
		try {
			
			Cliente cliente = clienteService.findById(id);
             //BORRAR FOTO ANTERIOR
			
			// varible que almacena el nombre_foto_anterior
			String nombreFotoAnterior = cliente.getFoto();
			
			uploadService.eliminar(nombreFotoAnterior);
			
			clienteService.delete(id);
			
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al Eliminar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
	        return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);// 404			
			
		}
		
		response.put("mensaje", "El cliente fue eliminado con exito");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		
	}
	
	
	// PARA obtener los datos que vienen en una peticion con form-data se coloca lo siguiente
	// @RequestParam("nombre_del_campo_en_la_peticion") MultipartFile <si es una archivo> 
	@Secured({"ROLE_USER","ROLE_ADMIN"})
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") long id){
		Map<String,Object> response = new HashMap<String,Object>();
		
		Cliente cliente = clienteService.findById(id);
		
		//validar que el archivo no este vacio
		if (!archivo.isEmpty()) {
			
			//obtener nombre del archivo
			//  Se le concatena UUID.randomUUID().toString() para que sea un nombre unico
			// archivo.getOriginalFilename().replace(" ", ""); : obtiene el nombre del archivo que se recibe de los parametros 
			// y se reamplaza los espacios en blanco por vacio
			
			// buscar el directorio del archivo : Paths.get("uploads");
			//.resolve(nombreArchivo): para completar la ruta con el nomre del archivo puede ser otra ruta
			// .toAbsolutePath() convertira en una sola ruta completa
			
			String nombreArchivo = null;
			
			try {
				//CON  Files.copy(archivo.getInputStream(), rutaArchivo) : coloca el archivo en el servidor usando la ruta que le especificamos
				nombreArchivo = uploadService.copiar(archivo);
			} catch (IOException e) {
				
				response.put("mensaje", "Error al subir la imagen del cliente " );
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
		        return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);// 404			

			}
			
			//BORRAR FOTO ANTERIOR
			
			// varible que almacena el nombre_foto_anterior
			String nombreFotoAnterior = cliente.getFoto();
			
			uploadService.eliminar(nombreFotoAnterior);
			
			
			// Si se guardo el archivo correctamente 
			// lo seteamos al cliente y lo guardamos en la base de datos
			cliente.setFoto(nombreArchivo);
			
			clienteService.save(cliente);
			
			response.put("cliente", cliente);
			response.put("mensaje", "Se ha guardado correctamente la imagen " + nombreArchivo);
			
		}
		
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);  // 201 created
	}
	
	
	// PARA enviar un archivo a la vista se debe crear un recurso  
	//con el objeto Resource de(springframework.core.io) 
	// SE debe retornar un ResponseEntity<Resource>
	//@Secured({"ROLE_USER","ROLE_ADMIN"})
	@GetMapping("/uploads/img/{nombreFoto:.+}") // {nombreFoto:.+} tipo de expresion regular que indica que incluira el punto y su extencion
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
		
		Resource recurso = null;
		
		
		try {
			recurso = uploadService.cargar(nombreFoto);
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
		
		// PARA forzar la descarga de la imagen  se debe crear una cabecera con
		// HttpHeaders y agregarlecela con el metodo add(key,val) donde:
		// key= Content-Disposition , val = "attachment; filename=\"" + recurso.getFilename() + "\""
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");
		
		return new ResponseEntity<Resource>(recurso, cabecera,HttpStatus.OK);
		
	}
	@Secured({"ROLE_ADMIN"})
	@GetMapping("/clientes/regiones")
	public List<Region> listarRegiones(){
		
		return clienteService.finAllRegiones();
		
	}
	
}
