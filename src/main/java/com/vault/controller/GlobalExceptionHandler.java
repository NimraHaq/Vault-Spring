package com.vault.controller;

import com.vault.errorresponse.VaultErrorResponse;
import com.vault.exceptions.CardCouldNotBeGeneratedException;
import com.vault.exceptions.CardNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//this handler pre-analyses the request being sent to the controllers and post-analyzes the response
//and handles the exceptions thrown from the controllers
//common exception handling code

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<VaultErrorResponse> cardNotFoundExceptionHandler(CardNotFoundException e){
        VaultErrorResponse error = new VaultErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<VaultErrorResponse> cardCouldNotBeGeneratedExceptionHandler(CardCouldNotBeGeneratedException e){
        VaultErrorResponse error = new VaultErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //generic exceptions handler
    @ExceptionHandler
    public ResponseEntity<VaultErrorResponse> handleException(Exception e){
        VaultErrorResponse error = new VaultErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}