package com.shoppingcart.service;

import com.shoppingcart.dto.AddressDto;
import com.shoppingcart.dto.AuthResponse;
import com.shoppingcart.entity.Address;
import com.shoppingcart.entity.User;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.repository.AddressRepository;
import com.shoppingcart.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public AuthResponse findUserByEmail(String email) throws UserNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No user found"));
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getFullName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole().name())
                .build();

    }

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public String update(String userEmail, String username, Long mobile) throws UserNotFoundException {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("Unauthorized"));
        if (username != null) {
            user.setFullName(username);
        }

        if (mobile != null) {
            user.setMobile(mobile);
        }

        userRepository.save(user);
        return "Updated Successfully";
    }

    public AddressDto addAddress(String email, AddressDto address) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Unauthorized"));

        if (address.getId() != null) {
            var savedAddress = addressRepository.findById(address.getId()).get();
            return updateAddress(address, savedAddress);
        }


        Address address1 = Address.builder()
                .houseName(address.getHouseName())
                .locality(address.getLocality())
                .district(address.getDistrict())
                .state(address.getState())
                .zip(address.getZip())
                .user(user)
                .build();
        var saved = addressRepository.save(address1);
        address.setId(saved.getId());

        return address;
    }

    private AddressDto updateAddress(AddressDto address, Address savedAddress) {
        savedAddress.setHouseName(address.getHouseName());
        savedAddress.setLocality(address.getLocality());
        savedAddress.setDistrict(address.getDistrict());
        savedAddress.setState(address.getState());
        savedAddress.setZip(address.getZip());

         savedAddress = addressRepository.save(savedAddress);
        address.setId(savedAddress.getId());
        return address;
    }

    public AddressDto getUserAddress(String userEmail) throws UserNotFoundException {
        var user = userRepository.findByEmail(userEmail).orElseThrow(()->new UserNotFoundException("Unauthorized"));
        var address = addressRepository.findByUser(user);

        if(address == null) {
            return new AddressDto();
        }

        return AddressDto.builder()
                .id(address.getId())
                .houseName(address.getHouseName())
                .locality(address.getLocality())
                .district(address.getDistrict())
                .state(address.getState())
                .zip(address.getZip())
                .build();
    }
}
