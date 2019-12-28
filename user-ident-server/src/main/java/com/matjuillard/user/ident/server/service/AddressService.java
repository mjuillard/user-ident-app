package com.matjuillard.user.ident.server.service;

import java.util.List;

import com.matjuillard.user.ident.server.model.dto.AddressDto;

public interface AddressService {

	public List<AddressDto> getAddresses(String userId);

	public AddressDto getAddress(String addressId);
}
