package com.shoppingcart.repository;

import com.shoppingcart.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
//    Address findByUser(User user);

}
