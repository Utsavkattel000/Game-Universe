package com.ecommerce.gaming.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.gaming.model.RedeemCodes;
import com.ecommerce.gaming.model.User;
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

	@Override
	public RedeemCodes getOneCode(Long gameid) {
		
		return redeemCodeRepo.findRandomCodeByGameIdAndNoBuyer(gameid);
	}

	@Override
	public List<RedeemCodes> getRedeemCodesByUserId(User user) {
		
		return redeemCodeRepo.getByUser(user);
	}

}
