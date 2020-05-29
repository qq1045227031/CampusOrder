package com.imooc.o2o.exceptions;

public class LocalAuthOperationException extends RuntimeException{
    private static final long serialVersionUID = 236144688482298905L;
    public LocalAuthOperationException(String message) {
        super(message);
    }
}
