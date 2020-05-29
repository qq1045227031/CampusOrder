package com.imooc.o2o.exceptions;
public class ProductCategoryOperationException extends RuntimeException {
    private static final long serialVersionUID = 236144688482298905L;
    public ProductCategoryOperationException(String message) {
        super(message);
    }
}