package com.example.PasswordEmailSecurityDemo.Exceptions;

import com.example.PasswordEmailSecurityDemo.ModelResponse.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class AppExceptionsHandler {

//    @ExceptionHandler(value = {UserServiceExceptions.class, NullPointerException.class})
//    public ResponseEntity<Object> handleUserServiceException(
//            Exception e, WebRequest webRequest)
    @ExceptionHandler(value = {UserServiceExceptions.class})
    public ResponseEntity<Object> handleUserServiceException(
            UserServiceExceptions userServiceExceptions, WebRequest webRequest)
    {

        ErrorMessage errorMessage= new ErrorMessage(new Date(),userServiceExceptions.getMessage());

       // return new ResponseEntity<>(userServiceExceptions,
        return new ResponseEntity<>(errorMessage,
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherException(
            Exception exception, WebRequest webRequest)
    {
        ErrorMessage errorMessage= new ErrorMessage(new Date(),exception.getMessage());
        return new ResponseEntity<>(errorMessage,
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
