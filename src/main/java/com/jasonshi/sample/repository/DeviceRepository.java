package com.jasonshi.sample.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.jasonshi.sample.entity.Device;

@RepositoryRestResource(collectionResourceRel = "device", path = "device")
public interface DeviceRepository extends PagingAndSortingRepository<Device, Long> {

	List<Device> findByName(@Param("name") String name);

}