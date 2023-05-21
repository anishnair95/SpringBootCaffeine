package com.javatechie.crud.example.controller;

import com.javatechie.crud.example.Exception.InvalidProductException;
import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {


    private final IProductService iProductService;

    private TransactionTemplate transactionTemplate;

    @Autowired
    public ProductController(IProductService iProductService, TransactionTemplate transactionTemplate) {
        this.iProductService = iProductService;
        this.transactionTemplate = transactionTemplate;
    }

    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product) throws InvalidProductException {
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

        try {
            Product productData = iProductService.updateProduct(product);
            if(productData==null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Product doesn't exist");
            }
            return  productData;
        }
        catch (InvalidProductException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
        }
        catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
        }

    }

//
//    @PutMapping("/products")
//    public Map<String, Object> updateProduct(@RequestBody Product product) throws InvalidProductException {
//
//        Map<String, Object> response = new HashMap<>();
//        Map<String, Object> error = new HashMap<>();
//
//
//        Product productData = transactionTemplate.execute(transactionStatus -> {
//
//            Product productData1 = null;
//            try {
//                productData1 = iProductService.updateProduct(product);
//            } catch (InvalidProductException e) {
//                error.put("reason", e.getMessage());
//            }
//            return productData1;
//        });
//
//        if( productData != null) {
//            response.put("success", true);
//            response.put("result", productData);
//        } else {
//            if( productData == null && error.isEmpty()) {
//                response.put("success", false);
//                error.put("reason", "Product does not exist");
//                response.put("error", error);
//            }
//            else {
//                response.put("sucess", false);
//                response.put("error", error);
//            }
//
//        }
//
//        return response;
////            return productData;
//
//    }
    @DeleteMapping("/products/{id}")
    public String deleteProduct(@PathVariable int id) {
        return iProductService.deleteProduct(id);
    }


    @PostMapping("/bulk/products/name")
    public List<Product> getAllProductsByName(@RequestBody Map<String, Object> data) {

        List<String>names = (List<String>) data.get("names");
        return iProductService.getAllProductByNames(names);

    }
}
