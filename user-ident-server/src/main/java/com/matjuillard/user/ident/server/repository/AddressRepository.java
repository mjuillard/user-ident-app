package com.matjuillard.user.ident.server.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.matjuillard.user.ident.server.model.entity.AddressEntity;
import com.matjuillard.user.ident.server.model.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

	List<AddressEntity> findAllByUser(UserEntity user);

	AddressEntity findByAddressId(String addressId);

}
