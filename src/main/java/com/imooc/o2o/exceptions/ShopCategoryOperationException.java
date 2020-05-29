package com.imooc.o2o.exceptions;

public class ShopCategoryOperationException extends RuntimeException{
    private static final long serialVersionUID = 236144688482298905L;
    public ShopCategoryOperationException(String message) {
        super(message);
    }
}
