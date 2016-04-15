package com.jasonshi.sample.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.jasonshi.sample.entity.Role;

@RepositoryRestResource(collectionResourceRel = "role", path = "role")
public interface RoleRepository extends CrudRepository<Role, Long>{
	Role findByName(@Param("name") String name);
}
