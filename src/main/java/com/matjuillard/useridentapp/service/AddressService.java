package com.matjuillard.useridentapp.service;

import java.util.List;

import com.matjuillard.useridentapp.model.dto.AddressDto;

public interface AddressService {

	public List<AddressDto> getAddresses(String userId);

	public AddressDto getAddress(String addressId);
}
