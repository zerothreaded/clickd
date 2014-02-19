package com.clickd.server.dao;

import org.springframework.data.repository.CrudRepository;

import com.clickd.server.model.Resource;

public class ResourceDao implements CrudRepository<Resource, String>{

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void delete(String arg0) {
		
	}

	@Override
	public void delete(Resource arg0) {
		
	}

	@Override
	public void delete(Iterable<? extends Resource> arg0) {
		
	}

	@Override
	public void deleteAll() {
		
	}

	@Override
	public boolean exists(String arg0) {
		return false;
	}

	@Override
	public Iterable<Resource> findAll() {
		return null;
	}

	@Override
	public Iterable<Resource> findAll(Iterable<String> arg0) {
		return null;
	}

	@Override
	public Resource findOne(String arg0) {
		return null;
	}

	@Override
	public <S extends Resource> S save(S arg0) {
		return null;
	}

	@Override
	public <S extends Resource> Iterable<S> save(Iterable<S> arg0) {
		return null;
	}

}
