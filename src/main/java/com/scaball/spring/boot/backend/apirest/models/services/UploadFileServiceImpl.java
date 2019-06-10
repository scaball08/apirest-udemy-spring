package com.scaball.spring.boot.backend.apirest.models.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UploadFileServiceImpl implements IUploadFileService{
	
	private final Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class); 
	private static final String DIRECTORIO_UPLOAD = "uploads";

	@Override
	public Resource cargar(String nombreFoto) throws MalformedURLException {
        Path rutaArchivo = getPath(nombreFoto);
		
		//log con la ruta inicializada
		log.info(rutaArchivo.toString());
		
		
			// se crea  la instacia con la clase UrlResource y se le envia como
			// parametro la ruta transformada en una URI
		Resource recurso =  new UrlResource(rutaArchivo.toUri());
			
			// se le debe colocar el try y catch para captar si se creao bien el recurso
		
		
		// Se valida si el recurso existe y si es leible
		if(!recurso.exists() && !recurso.isReadable()) {
			 rutaArchivo = Paths.get("src/main/resources/static/images").resolve("no-user.png").toAbsolutePath();
			
				// se crea  la instacia con la clase UrlResource y se le envia como
				// parametro la ruta transformada en una URI
				recurso =  new UrlResource(rutaArchivo.toUri());
				
				// se le debe colocar el try y catch para captar si se creao bien el recurso
		
			
			log.error("Error no se pudo cargar la imagen");
		}
		return recurso;
	}

	@Override
	public String copiar(MultipartFile archivo) throws IOException {
		String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
		
		// buscar el directorio del archivo : Paths.get("uploads");
		//.resolve(nombreArchivo): para completar la ruta con el nomre del archivo puede ser otra ruta
		// .toAbsolutePath() convertira en una sola ruta completa
		Path rutaArchivo = getPath(nombreArchivo);
		log.info(rutaArchivo.toString());
		
		
			//CON  Files.copy(archivo.getInputStream(), rutaArchivo) : coloca el archivo en el servidor usando la ruta que le especificamos
			Files.copy(archivo.getInputStream(), rutaArchivo);
		
		return nombreArchivo;
	}

	@Override
	public boolean eliminar(String nombreFoto) {
		if (nombreFoto!= null && nombreFoto.length()>0) {
			
			//Crear la ruta actual de la foto existente
			Path rutaFotoAnterior = getPath(nombreFoto);
			
			// crar un objeto de tipo File( de java.io) mediante la ruta de la foto anterior
			File archivoFotoAnterior = rutaFotoAnterior.toFile();
			
			// Se valida que el archivo exista
			if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete(); // se elimina el archivo
				
				return true;
				
			}
		}
		return false;
	}

	@Override
	public Path getPath(String nombreFoto) {
		
		return Paths.get(DIRECTORIO_UPLOAD).resolve(nombreFoto).toAbsolutePath();
	}

}
