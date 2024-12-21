package com.ecommerce.gaming.service;

import java.util.List;

import com.ecommerce.gaming.model.RedeemCodes;
import com.ecommerce.gaming.model.User;

public interface RedeemCodesService {
   void save(RedeemCodes redeemCode);
   
   RedeemCodes getOneCode(Long gameid);
   
   List<RedeemCodes> getRedeemCodesByUserId(User user);
}
