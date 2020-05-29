package com.imooc.o2o.exceptions;
//用于处理Shop的异常
public class ShopOperationException extends RuntimeException {
    private static final long serialVersionUID = 236144688482298905L;
    public ShopOperationException(String message) {
        super(message);
    }
}
