package com.esgglobal.order_history_loader.aspect;

import com.esgglobal.order_history_loader.dto.Response;
import com.esgglobal.order_history_loader.exceptions.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

import static com.esgglobal.order_history_loader.constants.Constants.ERROR;
import static com.esgglobal.order_history_loader.constants.Constants.ERROR_CODE;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> handleOrderNotFound(OrderNotFoundException ex, WebRequest request) {


        return new ResponseEntity<>(Response.builder()
                .description(ex.getMessage())
                .code(ERROR_CODE)
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleGeneral(IOException ex, WebRequest request) {

        return new ResponseEntity<>(Response.builder()
                .description(ex.getMessage())
                .code(ERROR_CODE)
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex, WebRequest request) {

        return new ResponseEntity<>(Response.builder()
                .description(ex.getMessage())
                .code(ERROR_CODE)
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
