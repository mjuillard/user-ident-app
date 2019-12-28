package com.matjuillard.user.ident.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matjuillard.user.ident.server.model.dto.AddressDto;
import com.matjuillard.user.ident.server.model.entity.AddressEntity;
import com.matjuillard.user.ident.server.model.entity.UserEntity;
import com.matjuillard.user.ident.server.repository.AddressRepository;
import com.matjuillard.user.ident.server.repository.UserRepository;
import com.matjuillard.user.ident.server.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> addresses = new ArrayList<AddressDto>();

		UserEntity user = userRepository.findByUserId(userId);
		if (user == null) {
			return addresses;
		}

		List<AddressEntity> addressesEntity = addressRepository.findAllByUser(user);
		for (AddressEntity addressEntity : addressesEntity) {
			addresses.add(new ModelMapper().map(addressEntity, AddressDto.class));
		}

		return addresses;
	}

	@Override
	public AddressDto getAddress(String addressId) {
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		if (addressEntity == null) {
			return null;
		}
		return new ModelMapper().map(addressEntity, AddressDto.class);
	}

}
