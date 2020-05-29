package com.imooc.o2o.util;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class EncryptPropertyPlaceholderConfigurer extends
        PropertyPlaceholderConfigurer {
    //需要加密的字段数组 这里是数控连接的账号密码
    private String[] encryptPropNames = { "jdbc.username", "jdbc.password" };
//对关键的师行进行转换 每个属性都会进入该方法进行判断和解密
    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        if (isEncryptProp(propertyName)) {
            //解密工具解密
            String decryptValue = DESUtils.getDecryptString(propertyValue);
            return decryptValue;
        } else {
            return propertyValue;
        }
    }

    private boolean isEncryptProp(String propertyName) {
        for (String encryptpropertyName : encryptPropNames) {
            if (encryptpropertyName.equals(propertyName))
                return true;
        }
        return false;
    }
}

