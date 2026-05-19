package com.producto.ms_producto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensaje){
        super(mensaje);
    }
}
