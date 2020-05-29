package com.imooc.o2o.exceptions;

public class ProductOperationException extends RuntimeException {
    private static final long serialVersionUID = 236144688482298905L;
    public ProductOperationException(String message) {
        super(message);
    }
}
