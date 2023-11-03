package com.shoppingcart.exception;


import lombok.Getter;

@Getter
public class ProductException extends Exception{
    Integer status;
    public ProductException(String msg, Integer status) {
        super(msg);
        this.status = status;
    }
}
