package com.shoppingcart.repository;

import com.shoppingcart.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokensRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUserId(Long userId);
    RefreshToken findByRefreshToken(String token);
}
