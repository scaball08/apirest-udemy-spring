package com.scaball.spring.boot.backend.apirest.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scaball.spring.boot.backend.apirest.models.dao.IClienteDao;
import com.scaball.spring.boot.backend.apirest.models.entity.Cliente;
import com.scaball.spring.boot.backend.apirest.models.entity.Region;

@Service
public class ClienteServiceImpl implements IClienteService {
	
	//Se inyecta el IClienteDao con @Autowired
	
	@Autowired
	private IClienteDao clientedao;
	
	//Con la notacion @Transactional  podemos indicar el tipo de transaccion que realizamos
	@Override
	@Transactional(readOnly=true)
	public List<Cliente> findAll() {
		
	//clientedao.findAll() : retorna un iterable asi que hay que hacerle un cast (List<Cliente>)
	
		return (List<Cliente>) clientedao.findAll();
	}
	
	@Override
	@Transactional(readOnly=true)
	public Page<Cliente> findAll(Pageable pageable) {
		
		return clientedao.findAll(pageable);
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente findById(long id) {
		
		return clientedao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Cliente save(Cliente cliente) {
		
		return clientedao.save(cliente);
	}

	@Override
	@Transactional
	public void delete(long id) {
		
		clientedao.deleteById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Region> finAllRegiones() {
		
		return clientedao.finAllRegiones();
	}

	

}
