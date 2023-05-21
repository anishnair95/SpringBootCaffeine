package com.javatechie.crud.example.service;

import com.javatechie.crud.example.Exception.InvalidProductException;
import com.javatechie.crud.example.customKey.CustomKeyGenerator;
import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@CacheConfig(cacheNames = {"productCache"})
public class ProductService implements IProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final CustomKeyGenerator customKeyGenerator;

    @Autowired
    public ProductService(ProductRepository productRepository, CustomKeyGenerator customKeyGenerator) {
        this.productRepository = productRepository;
        this.customKeyGenerator = customKeyGenerator;
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
    public Product saveProduct(Product product) throws InvalidProductException {

        if (product.getQuantity() < 1 ) {
            throw new InvalidProductException();
        }
        return productRepository.save(product);
    }

    //Get All Products
//    @Cacheable(unless = "#result==null", keyGenerator = "customKeyGenerator")
    public List<Product> getProducts() {
        LOGGER.info("Hitting DB for 1st time");
        return productRepository.findAll();
    }

    //Get Product By Id
//    @Cacheable(value = "productCache",unless="#result==null", keyGenerator = "customKeyGenerator")
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

    //@CachePut only works in Caffeine without @CacheEvict
//    @CachePut(value="productCache",unless = "#result==null", keyGenerator = "customKeyGenerator")
    public Product updateProduct(Product product) throws InvalidProductException {

        LOGGER.info("Executing updateProduct");


        Product existingProduct = productRepository.findById(product.getId()).orElse(null);

        if(existingProduct==null) {
            return null;
        }
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());

        if (product.getQuantity() < 3) {
            throw new InvalidProductException("Invalid Product Detail");
        }
        existingProduct.setQuantity(product.getQuantity());
        return productRepository.save(existingProduct);
    }


    //Delete all the cache entry. So fetch will store new cache on method invocation
//    @CacheEvict(value="productCache",allEntries = true)
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

}
