package com.shoppingcart.repository;

import com.shoppingcart.entity.Address;
import com.shoppingcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByUser(User user);
}
