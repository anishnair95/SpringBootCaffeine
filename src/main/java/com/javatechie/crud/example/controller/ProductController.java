package com.javatechie.crud.example.controller;

import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.service.IProductService;
import com.javatechie.crud.example.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {


    private final IProductService iProductService;

    private static final Logger LOGGER = LoggerFactory.getLogger(IProductService.class);
    @Autowired
    public ProductController(IProductService iProductService) {
        this.iProductService = iProductService;
    }

    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product) {
        Product productData = iProductService.saveProduct(product);
        if(productData==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return  productData;
    }

    @PostMapping("/bulk/products")
    public List<Product> addProducts(@RequestBody List<Product> products) {
        List<Product> productList = iProductService.saveProducts(products);

        if(productList==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return  productList;
    }

    @GetMapping("/products")
    public List<Product> findAllProducts() {
        List<Product> productList = iProductService.getProducts();

        if(productList==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return  productList;
    }

    @GetMapping("/products/{id}")
    public Product findProductById(@PathVariable int id) {
        Product productData = iProductService.getProductById(id);

        if(productData==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return  productData;

    }

    @GetMapping("/products/name/{name}")
    public Product findProductByName(@PathVariable String name) {
        Product productData = iProductService.getProductByName(name);

        if(productData==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return  productData;
    }

    @PutMapping("/products")
    public Product updateProduct(@RequestBody Product product) {
        Product productData = iProductService.updateProduct(product);
        if(productData==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return  productData;

    }

    @DeleteMapping("/products/{id}")
    public String deleteProduct(@PathVariable int id) {
        return iProductService.deleteProduct(id);
    }


    @PostMapping("/bulk/products/name")
    public List<Product> getAllProductsByName(@RequestBody Map<String, Object> data) {

        List<String>names = (List<String>) data.get("names");
        return iProductService.getAllProductByNames(names);

    }

    //API to download db data in CSV
    @GetMapping("/products/download")
    public String downloadCSV() {

        return "CSV downloaded successfully!!";
    }

    //API to download db data in CSV
    @PostMapping("/products/download")
    public ResponseEntity<Resource> downloadCSV(@RequestBody Map<String, Object> requestBody) {
        try {

            /* One way of checking the type instead of directly type casting
//            List<String> ids = new ArrayList<>();
//
//            //type case check
//            Object object = requestBody.get("ids");
//            if (object instanceof List) {
//
//                for( Object o :(List<?>) object) {
//                    if (o instanceof String) {
//                        ids.add((String) o);
//                    } else {
//                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid object");
//
//                    }
//                }
//            }
             */

            List rawList = null;
            if (requestBody.get("ids") instanceof List) {
                rawList = (List) requestBody.get("ids");
            }
            List<String> ids = rawList != null ? ClassUtils.castList(String.class, rawList) : Collections.emptyList();

            LOGGER.info("The list of received ids are: {}", ids);

            ByteArrayInputStream bis = iProductService.createCSV(ids);

            Resource resource = bis != null ? new InputStreamResource(bis) : null;
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Products.csv");
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, "text/csv");
            if (resource != null) {
                return new ResponseEntity<>(resource, httpHeaders, HttpStatus.ACCEPTED);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data exists");

            }

        } catch (ClassCastException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
