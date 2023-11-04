package com.shoppingcart.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductException extends Exception{
    HttpStatus status;
    public ProductException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }
}
