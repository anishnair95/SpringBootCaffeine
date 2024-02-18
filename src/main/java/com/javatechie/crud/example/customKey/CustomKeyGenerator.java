package com.javatechie.crud.example.customKey;

import static org.apache.commons.codec.digest.DigestUtils.sha512Hex;

import com.javatechie.crud.example.service.ProductService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@Component
public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {

        String key = target.getClass().getSimpleName()+method.getName()+ Arrays.toString(params);

//        return target.getClass().getSimpleName()+ "_" +method.getName()+ "_"+
//                StringUtils.arrayToDelimitedString(params,"_");
//
//        return Objects.hash(params);

        return sha512Hex(key);

    }

//    public static void main(String args[]) throws NoSuchMethodException {
//        CustomKeyGenerator customKeyGenerator = new CustomKeyGenerator();
//
//        Method method = ProductService.class.getDeclaredMethod("getProducts");
//        method.setAccessible(true);
//
//        Object key = customKeyGenerator.generate(CustomKeyGenerator.class, method, 1);
//
//        System.out.println(key.toString());
//    }
}
