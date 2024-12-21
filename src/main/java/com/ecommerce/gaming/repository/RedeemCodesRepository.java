package com.ecommerce.gaming.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.gaming.model.RedeemCodes;
import com.ecommerce.gaming.model.User;

public interface RedeemCodesRepository extends JpaRepository<RedeemCodes, Long> {

	@Query(value = "SELECT * FROM redeem_codes " + "WHERE game_id = :gameId AND buyer_id IS NULL "
			+ "ORDER BY RAND() LIMIT 1", nativeQuery = true)
	RedeemCodes findRandomCodeByGameIdAndNoBuyer(@Param("gameId") long gameId);
	
	List<RedeemCodes> getByUser(User user);

}
