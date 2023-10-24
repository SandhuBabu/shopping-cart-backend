package com.shoppingcart.exception.handlers;


import com.shoppingcart.dto.ErrorDto;
import com.shoppingcart.exception.TokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(value = TokenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorDto jwtExpired(TokenException e) {
        return new ErrorDto(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

}
