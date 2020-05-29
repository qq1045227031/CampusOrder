package com.imooc.o2o.exceptions;

public class WechatOperationException extends RuntimeException {
    private static final long serialVersionUID = 236144688482298905L;
    public WechatOperationException(String message) {
        super(message);
    }
}
