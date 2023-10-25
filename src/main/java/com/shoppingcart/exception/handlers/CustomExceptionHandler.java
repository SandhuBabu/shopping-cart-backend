package com.shoppingcart.exception.handlers;


import com.shoppingcart.dto.ErrorDto;
import com.shoppingcart.exception.TokenException;
import com.shoppingcart.exception.UserCreationException;
import com.shoppingcart.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = TokenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorDto jwtExpired(TokenException e) {
        return new ErrorDto(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ExceptionHandler(value = UserCreationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorDto userCreationFailed(UserCreationException e) {
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }


    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorDto badCredentials(BadCredentialsException e) {
        return new ErrorDto(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorDto userNotFound(UserNotFoundException e) {
        return new ErrorDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto globalHandler(Exception e) {
        e.printStackTrace();
        System.out.println("ERROR ::: "+ e.getMessage());
        return new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

}
