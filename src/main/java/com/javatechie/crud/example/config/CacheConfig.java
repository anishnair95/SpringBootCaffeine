package com.javatechie.crud.example.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * This class contains the bean configuration for caffeine cache
 */
@Configuration
public class CacheConfig {

//    //creates CaffeineCache builder
//    @Bean
//    public Caffeine caffeine() {
//
//        return Caffeine.newBuilder()
//                .expireAfterWrite(30, TimeUnit.MINUTES)
//                .maximumSize(100);
//    }
//
//
//    //SimpleCacheManager that will return caffeine cache
//    @Bean
//    public CacheManager simpleCacheManager() {
//
//        //creating cache with cache name
//        CaffeineCache productCache  = new CaffeineCache("productCache",caffeine().build());
//        CaffeineCache userCache  = new CaffeineCache("userCache",caffeine().build());
//
//        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
//        simpleCacheManager.setCaches(Arrays.asList(productCache,userCache));
//        return simpleCacheManager;
//
//    }

    //Custom CaffeineCache manager with caffeine cache builder settings
//    @Bean
//    public CaffeineCacheManager caffeineCacheManager() {
//
//        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
////        caffeineCacheManager.setCacheNames(Arrays.asList("productCache","userCache"));
////
////        caffeineCacheManager.setCaffeine(caffeine());
//
//        return caffeineCacheManager;
//
//    }
}
