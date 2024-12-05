package com.ecommerce.gaming.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.gaming.model.RedeemCodes;
import com.ecommerce.gaming.repository.RedeemCodesRepository;
import com.ecommerce.gaming.service.RedeemCodesService;

@Service
public class RedeemCodesServiceImpl implements RedeemCodesService{

	@Autowired
	RedeemCodesRepository redeemCodeRepo;
	
	@Override
	public void save(RedeemCodes redeemCode) {
		
		redeemCodeRepo.save(redeemCode);
	}

}
