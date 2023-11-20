package com.shoppingcart.dto;

import com.shoppingcart.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private Long id;
    private String houseName;
    private String locality;
    private String district;
    private String state;
    private Long zip;
}
