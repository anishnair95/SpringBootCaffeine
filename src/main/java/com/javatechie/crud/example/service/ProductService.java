package com.javatechie.crud.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.benmanes.caffeine.cache.Cache;
import com.javatechie.crud.example.customKey.CustomKeyGenerator;
import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.repository.ProductRepository;
import com.javatechie.crud.example.util.CSVUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.Cache.ValueWrapper;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {"productCache", "settingCache"})
public class ProductService implements IProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final CustomKeyGenerator customKeyGenerator;

    private final CacheManager cacheManager;

    public static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ProductService(ProductRepository productRepository, CustomKeyGenerator customKeyGenerator, CacheManager cacheManager) {
        this.productRepository = productRepository;
        this.customKeyGenerator = customKeyGenerator;
        this.cacheManager = cacheManager;
    }

    //BulkSave
    //Delete all the cache entry. So fetch will store new cache on method invocation

//    @CacheEvict(value="productCache",allEntries = true)
    public List<Product> saveProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    //Single Product Save
    //Delete all the cache entry. So fetch will store new cache on method invocation

//    @CacheEvict(value="productCache",allEntries = true)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    //Get All Products
//    @Cacheable(unless = "#result==null", keyGenerator = "customKeyGenerator")
    public List<Product> getProducts() throws JsonProcessingException {

        if (cacheManager == null) {
            throw new RuntimeException("Cache manager is not present");
        }
        System.out.println("Cache manager is present");
        System.out.println("List of cache present:" + cacheManager.getCacheNames());
        Cache settingCache = cacheManager.getCache("settingCache");
        Cache productCache = cacheManager.getCache("productCache");

        if (settingCache == null) {
            System.out.println("settingsCache is empty");
        } else {
            System.out.println("hurray!! settingsCache is present");
            settingCache.putIfAbsent("datatest", "hurray added it finally");
        }

        if (productCache == null) {
            System.out.println("productcache is empty");
        } else {
            System.out.println("hurray!! productCache is present");
        }

        if (settingCache != null) {
            // Try to retrieve from cache
            ValueWrapper cachedResponse = settingCache.get("datatest");
            if (cachedResponse != null) {
                System.out.println("Settings: datatest=" + cachedResponse.get());
            }
        }

        // key generation begin
        Method method;
        try {
            method = this.getClass().getMethod("getProducts");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Object key = customKeyGenerator.generate(ProductService.class, method, null);
        // key generation end

        if (productCache != null) {
            ValueWrapper cachedResponse = productCache.get(key);

            if (cachedResponse != null) {
                Object cacheResponse = cachedResponse.get();
                if (cacheResponse instanceof List) {
                    System.out.println("Fetching from cache");
                    return (List<Product>) cacheResponse;
                }
            }
        }

        LOGGER.info("Hitting DB for 1st time");
        List<Product> products = productRepository.findAll();
        //using hashkey to store data and will retrieve it in next call
        if (!products.isEmpty()) {
            productCache.put(key, products);
        }
        return products;
    }

    //Get Product By Id
    @Cacheable(value = "productCache",unless="#result==null", keyGenerator = "customKeyGenerator")
    public Product getProductById(int id) {

        LOGGER.info("Hitting DB for 1st time");
        return productRepository.findById(id).orElse(null);
    }


    //Get Product By Name

    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }


    //Update Product

    //cache if result is not null with key as id
    //Delete all the cache entry. So fetch will store new cache on method invocation
//    @CacheEvict(value="productCache",allEntries = true)
    @CachePut(value="productCache",unless = "#result==null", keyGenerator = "customKeyGenerator")
    public Product updateProduct(Product product) {

        LOGGER.info("Hitting DB for 1st time");
        Product existingProduct = productRepository.findById(product.getId()).orElse(null);

        if(existingProduct==null) {
            return null;
        }
        existingProduct.setName(product.getName());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        return productRepository.save(existingProduct);
    }


    //Delete all the cache entry. So fetch will store new cache on method invocation
    @CacheEvict(value="productCache",allEntries = true)
    public String deleteProduct(int id) {
        LOGGER.info("Hitting DB for 1st time");
        Product productData = getProductById(id);

        if(productData!=null) {
            productRepository.deleteById(id);
            return "product removed !! " + id;
        }

        return "No product exist with id !! " + id;
    }


    @Override
    public List<Product> getAllProductByNames(List<String> names) {

        return productRepository.findByNameIn(names);
    }


    //donwload csv using apache-commons-csv library

    @Override
    public ByteArrayInputStream createCSV(List<String> ids) {

        //fetch all records
        // add to csv
        // return as response;

        List<Integer> productIds = convertToInteger(ids);
        List<Product> products = productRepository.findByIdIn(productIds);

        LOGGER.info("Products size = {}", products.size());
        ByteArrayInputStream bis = products.size() > 0 ? CSVUtils.beanToCSV(products) : null;

        return bis;
    }

    private List<Integer> convertToInteger(List<String> ids) {
        return ids.stream()
                .map(Integer::parseInt).collect(Collectors.toList());
    }

}
