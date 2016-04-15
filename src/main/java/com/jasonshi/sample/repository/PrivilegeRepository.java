package com.jasonshi.sample.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.jasonshi.sample.entity.Privilege;

@RepositoryRestResource(collectionResourceRel = "privilege", path = "privilege")
public interface PrivilegeRepository extends CrudRepository<Privilege, Long>{
	Privilege findByName(@Param("name") String name);
}
